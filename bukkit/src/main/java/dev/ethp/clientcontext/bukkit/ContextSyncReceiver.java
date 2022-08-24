package dev.ethp.clientcontext.bukkit;

import dev.ethp.clientcontext.Constants;
import dev.ethp.clientcontext.messages.ContextRequest;
import dev.ethp.clientcontext.messages.ContextResponse;
import dev.ethp.clientcontext.messages.Message;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A LuckPerms {@link ContextCalculator} that communicates with the ClientContext BungeeCord plugin over a plugin
 * message channel, syncing the context info from the proxy (which has more accurate info about client status and
 * version).
 * <p>
 * This manages its own internal map of {@link UUID --> ContextSet}, ensuring that it will provide LuckPerms with
 * the correct context data, even when done through an asynchronous thread.
 */
public class ContextSyncReceiver implements Listener, PluginMessageListener, ContextCalculator<Player> {
	private final @NotNull JavaPlugin plugin;
	private final @NotNull Logger logger;
	private final @NotNull Map<UUID, ContextSet> contextMap;
	private final @NotNull LuckPerms luckperms;

	public ContextSyncReceiver(@NotNull JavaPlugin plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.contextMap = new ConcurrentHashMap<>();
		this.luckperms = LuckPermsProvider.get();
	}

	/**
	 * Sends a plugin message asking the BungeeCord plugin for a specific player's calculated contexts.
	 *
	 * @param player The player.
	 */
	public void requestContext(@NotNull Player player) {
		plugin.getServer().getScheduler().runTask(plugin, () -> {
			requestContext_UNSAFE(player);
		});
	}

	/**
	 * Sends a plugin message asking the BungeeCord plugin for all players' calculated contexts.
	 */
	public void requestContext() {
		plugin.getServer().getScheduler().runTask(plugin, () -> {
			for (var player : plugin.getServer().getOnlinePlayers()) {
				requestContext_UNSAFE(player);
			}
		});
	}

	/**
	 * Sends a plugin message asking the BungeeCord plugin for the player's calculated contexts.
	 *
	 * <p><b>This is not thread safe!</b>
	 *
	 * @param player The player.
	 */
	private void requestContext_UNSAFE(@NotNull Player player) {
		var request = new ContextRequest(player.getUniqueId());
		player.sendPluginMessage(
				plugin,
				Constants.CHANNEL,
				Message.gen(ContextRequest::serialize, ContextRequest.COMMAND, request)
		);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Message Handling:
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Syncs contexts when the proxy sends contexts over a plugin message channel.
	 *
	 * @param channel  The plugin message channel.
	 * @param receiver The player connection the message is being sent from.
	 * @param message  The serialized message bytes.
	 */
	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player receiver, byte[] message) {
		if (!channel.equalsIgnoreCase(Constants.CHANNEL)) return;

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			handleMessage(receiver, in.readUTF(), in);
			in.close();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Unable to process plugin message.", ex);
		}
	}

	@SuppressWarnings("UnnecessaryReturnStatement")
	protected void handleMessage(@NotNull Player receiver, @NotNull String command, @NotNull DataInputStream in) throws IOException {
		switch (command.toLowerCase()) {
			case ContextResponse.COMMAND:
				ContextResponse response = ContextResponse.deserialize(in);
				this.updateUser(response.user, response.contexts);
				return;

			case ContextRequest.COMMAND:
				logger.severe("WTF: Server should not have sent ContextResponse plugin message.");
				return;

			default:
				logger.severe("WTF: Received unknown plugin message: " + command);
				return;
		}
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Context Mapping:
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the contexts for a player.
	 *
	 * <p>This runs on the server thread to ensure that the player is online at the time.
	 *
	 * @param uuid     The UUID of the player.
	 * @param contexts The player's new contexts.
	 */
	private void updateUser(UUID uuid, ContextSet contexts) {
		for (var context : contexts) {
			if (!context.getKey().startsWith("client:")) {
				logger.severe("WTF: Received context for player " + uuid + " that was not namespaced to this plugin.");
				return;
			}
		}

		plugin.getServer().getScheduler().runTask(plugin, () -> {
			Player player = plugin.getServer().getPlayer(uuid);
			if (player == null || !player.isOnline()) {
				// Player must have disconnected?
				return;
			}

			ContextSet old = this.contextMap.put(uuid, contexts);
			if (!contexts.equals(old)) {
				this.luckperms.getContextManager().signalContextUpdate(player);
			}
		});
	}

	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		requestContext(event.getPlayer());
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		// Wait a bit, then remove the context from the map.

		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			this.contextMap.remove(event.getPlayer().getUniqueId());
		}, 1);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// LuckPerms:
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * Called by LuckPerms to get the player's context info.
	 * This will read the context set from the map.
	 *
	 * @param target   The player.
	 * @param consumer The context consumer.
	 */
	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
		ContextSet contexts = this.contextMap.get(target.getUniqueId());
		if (contexts != null) {
			consumer.accept(contexts);
		}
	}

}
