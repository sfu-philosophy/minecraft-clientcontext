package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A listener that sends a {@code client-context:sync} plugin message containing serialized contexts.
 * The serialized message format is:
 *
 * <pre>
 * [string] "sync"
 * [short] ITEM_COUNT
 * for ITEM_COUNT
 *     [string] CONTEXT_KEY
 *     [string] CONTEXT_VALUE
 * </pre>
 */
public class ContextSync implements Listener {
	private final Set<ContextCalculator<ProxiedPlayer>> calculators;
	private final Logger logger;

	public ContextSync(@NotNull Plugin plugin, @NotNull Set<ContextCalculator<ProxiedPlayer>> calculators) {
		this.calculators = calculators;
		this.logger = plugin.getLogger();
	}

	/**
	 * Syncs contexts when a plugin asks for context data.
	 * @param event The event.
	 */
	@EventHandler
	public void onMessage(PluginMessageEvent event) {
		if (!event.getTag().equals(Constants.CHANNEL)) return;
		String command;
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));

		try {
			command = in.readUTF();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "Unable to read plugin message command.");
			return;
		}

		// "fetch" command.
		// Asks the BungeeCord server to send over the calculated contexts.
		if (command.equalsIgnoreCase("fetch")) {
			if (event.getReceiver() instanceof ProxiedPlayer) {
				sendContexts((ProxiedPlayer) event.getReceiver());
				return;
			}
		}

		logger.log(Level.WARNING, "Unable to dispatch plugin message command '" + command + "'.");
	}

	/**
	 * Calculates the up-to-date contexts provided by this plugin and sends them over a plugin channel message.
	 *
	 * @param player The player to update.
	 */
	public void sendContexts(ProxiedPlayer player) {
		// Calculate the contexts to send.
		ContextDataConsumer builder = new ContextDataConsumer();
		for (var calculator : this.calculators) {
			calculator.calculate(player, builder);
		}

		// Serialize the generated contexts.
		ByteArrayOutputStream szBuffer = new ByteArrayOutputStream();
		try {
			DataOutputStream szWriter = new DataOutputStream(szBuffer);
			szWriter.writeUTF("sync");
			szWriter.writeShort(builder.contexts.size());
			for (var context : builder.contexts) {
				szWriter.writeUTF(context.getKey());
				szWriter.writeUTF(context.getValue());
			}
		} catch (IOException ex) {
			// This shouldn't happen.
			logger.log(Level.SEVERE, "Unable to generate serialized context buffer.");
			return;
		}

		// Send the context data to the client.
		player.getServer().getInfo().sendData(Constants.CHANNEL, szBuffer.toByteArray());
	}

}

