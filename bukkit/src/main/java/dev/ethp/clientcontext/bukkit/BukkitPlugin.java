package dev.ethp.clientcontext.bukkit;

import dev.ethp.clientcontext.CommonPlugin;
import dev.ethp.clientcontext.Constants;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the Bukkit plugin.
 */
public class BukkitPlugin extends JavaPlugin {
	private BukkitPlatform platform;
	private CommonPlugin<Player> common;
	private ContextSyncReceiver sync;

	@Override
	public void onEnable() {
		// Initialize everything.
		this.platform = new BukkitPlatform(this);
		this.common = new CommonPlugin<>(getLogger(), this.platform);

		// Register contexts.
		if (BungeeUtil.isRunningUnderBungee()) {
			this.sync = new ContextSyncReceiver(this);
			this.common.register(this.sync);
			getLogger().info("Running under a BungeeCord network; contexts will be synced from proxy for accuracy.");
			getLogger().info("If you do not have the ClientContext plugin installed on the proxy, please install it.");
		} else {
			this.common.registerDefaults();
		}

		// Register plugin messages.
		if (this.sync != null) {
			getServer().getPluginManager().registerEvents(this.sync, this);
			getServer().getMessenger().registerIncomingPluginChannel(this, Constants.CHANNEL, this.sync);
			getServer().getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL);
		}

		// Sync all client info.
		if (this.sync != null) {
			this.sync.requestContext();
		}
	}

	public void onDisable() {
		this.common.unregister();

		getServer().getMessenger().unregisterIncomingPluginChannel(this);
		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	}

}
