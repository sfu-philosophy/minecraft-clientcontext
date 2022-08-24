package dev.ethp.clientcontext.bungee;

import dev.ethp.clientcontext.CommonPlugin;
import dev.ethp.clientcontext.Constants;
import dev.ethp.clientcontext.messages.ContextRequest;
import dev.ethp.clientcontext.messages.ContextResponse;
import dev.ethp.clientcontext.messages.Message;
import net.luckperms.api.context.ContextSet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A listener that responds to plugin messages requesting the client context info for a given player.
 * This will only reply to servers within the network, and not players.
 *
 * @see ContextRequest
 * @see ContextResponse
 */
@SuppressWarnings("FieldCanBeLocal")
public class ContextSync implements Listener {
	private final CommonPlugin<ProxiedPlayer> common;
	private final Logger logger;
	private final ProxyServer server;

	public ContextSync(@NotNull Plugin plugin, @NotNull CommonPlugin<ProxiedPlayer> common) {
		this.common = common;
		this.logger = plugin.getLogger();
		this.server = plugin.getProxy();
	}

	/**
	 * Syncs contexts when a plugin asks for context data.
	 *
	 * @param event The event.
	 */
	@EventHandler
	public void onMessage(@NotNull PluginMessageEvent event) {
		if (!event.getTag().equalsIgnoreCase(Constants.CHANNEL)) return;
		if (event.getSender() instanceof ProxiedPlayer) {
			event.setCancelled(true);
			logger.severe("Client of player '" + ((ProxiedPlayer) event.getSender()).getName() + "' tried to use plugin channel.");
			return;
		}

		ProxiedPlayer player = (
				event.getReceiver() instanceof ProxiedPlayer
						? (ProxiedPlayer) event.getReceiver()
						: (ProxiedPlayer) event.getSender()
		);

		Server server = (
				event.getSender() instanceof Server
						? (Server) event.getSender()
						: (Server) event.getReceiver()
		);

		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
			handleMessage(event, player, server, in.readUTF(), in);
			in.close();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Unable to process plugin message.", ex);
		}
	}

	@SuppressWarnings("UnnecessaryReturnStatement")
	protected void handleMessage(@NotNull PluginMessageEvent event, @NotNull ProxiedPlayer player, @NotNull Server server, @NotNull String command, @NotNull DataInputStream in) {
		switch (command.toLowerCase()) {
			case ContextRequest.COMMAND:
				ContextSet contexts = common.calculate(player);
				ContextResponse response = new ContextResponse(player.getUniqueId(), contexts);
				server.getInfo().sendData(Constants.CHANNEL, Message.gen(ContextResponse::serialize, ContextResponse.COMMAND, response));
				return;

			case ContextResponse.COMMAND:
				logger.severe("WTF: Server should not have sent ContextResponse plugin message.");
				return;

			default:
				logger.severe("WTF: Received unknown plugin message: " + command);
				return;
		}
	}

}

