package dev.ethp.clientcontext.bungee;

import dev.ethp.clientcontext.CommonPlugin;
import dev.ethp.clientcontext.Constants;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

/**
 * Main class for the BungeeCord plugin.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BungeePlugin extends Plugin {
	private BungeePlatform platform;
	private CommonPlugin<ProxiedPlayer> common;


	@Override
	public void onEnable() {
		// Initialize everything.
		this.platform = new BungeePlatform(this, createVersionResolver());
		this.common = new CommonPlugin<>(getLogger(), this.platform);

		// Register contexts.
		this.common.registerDefaults();

		// Register listener to forward contexts to Bukkit servers.
		ProxyServer server = getProxy();
		server.registerChannel(Constants.CHANNEL);
		server.getPluginManager().registerListener(this, new ContextSync(this, this.common));
	}

	@Override
	public void onDisable() {
		this.common.unregister();

		// Unregister listeners.
		ProxyServer server = getProxy();
		server.unregisterChannel(Constants.CHANNEL);
		server.getPluginManager().unregisterListeners(this);
	}

	private ClientVersionResolver createVersionResolver() {
		try {
			return new ClientVersionResolver();
		} catch (ReflectiveOperationException | NumberFormatException t) {
			getLogger().log(Level.WARNING, "Unable to generate client version map.", t);
			return null;
		}
	}

}
