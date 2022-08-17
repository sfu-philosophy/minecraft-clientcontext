package dev.ethp.clientcontext;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BukkitPlugin extends JavaPlugin implements PluginMessageListener, Listener, ContextCalculator<Player> {

	@Override
	public void onEnable() {
		LuckPerms api = LuckPermsProvider.get();
		ContextManager manager = api.getContextManager();

		manager.registerCalculator(this);
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getMessenger().registerIncomingPluginChannel(this, Constants.CHANNEL, this);
		getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL);

		// Sync all client info.
		for (var player : getServer().getOnlinePlayers()) {
			this.requestContexts(player);
		}
	}

	public void onDisable() {
		LuckPerms api = LuckPermsProvider.get();
		ContextManager manager = api.getContextManager();

		manager.unregisterCalculator(this);
		getServer().getMessenger().unregisterIncomingPluginChannel(this);
		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}

	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		requestContexts(event.getPlayer());
	}

	/**
	 * Called by LuckPerms to get the player's context info.
	 * This will read the context set from the player's metadata.
	 *
	 * @param target The player.
	 * @param consumer The context consumer.
	 */
	@SuppressWarnings({"ConstantConditions", "unchecked"})
	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
		for (var metadata : target.getMetadata(Constants.METADATA_KEY)) {
			if (metadata.getOwningPlugin() != this) continue;
			for (var context : (Set<ContextData>) metadata.value()) {
				consumer.accept(context);
			}
		}
	}

	/**
	 * Sends a plugin message to the BungeeCord plugin, asking for it to send over the player's client information.
	 *
	 * @param player The player.
	 */
	public void requestContexts(@NotNull Player player) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(buffer);
			out.writeUTF("fetch");

			player.sendPluginMessage(this, Constants.CHANNEL, buffer.toByteArray());
		} catch (Exception ex) {
			getLogger().log(
					Level.WARNING,
					"Unable to request contexts for player " + player.getName() + "(" + player.getUniqueId() + ")",
					ex
			);
		}
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
		if (!channel.equals(Constants.CHANNEL)) return;
		Set<ContextData> contexts = new HashSet<>();

		// Read the contexts from the BungeeCord plugin.
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			String command = in.readUTF();
			if (!command.equalsIgnoreCase("sync")) {
				return;
			}

			for (int i = 0, max = in.readShort(); i < max; i++) {
				String key = in.readUTF();
				String value = in.readUTF();

				if (!key.startsWith("client-")) {
					// If it doesn't start with "client-", don't set the context for security reasons.
					// All contexts from the plugin should start with that.
					getLogger().warning("Something tried to send a context that isn't managed by ClientContext.");
					getLogger().warning("The user in question is " + player.getName() + " (" + player.getUniqueId() + ")");
					continue;
				}

				contexts.add(new ContextData(key, value));
			}
		} catch (Exception ex) {
			getLogger().log(Level.WARNING, "Unable to read contexts from ClientContext BungeeCord plugin.", ex);
			return;
		}

		// Update the player's contexts.
		player.setMetadata(Constants.METADATA_KEY, new FixedMetadataValue(this, contexts));
		LuckPermsProvider.get().getContextManager().signalContextUpdate(player);
	}

}
