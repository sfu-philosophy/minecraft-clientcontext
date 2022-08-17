package dev.ethp.clientcontext;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BungeePlugin extends Plugin {
	private Set<ContextCalculator<ProxiedPlayer>> calculators;
	private ContextManager manager;

	@Override
	public void onEnable() {
		this.calculators = new HashSet<>();
		this.manager = LuckPermsProvider.get().getContextManager();

		// Register contexts.
		registerClientTypeCalculator();
		registerClientVersionCalculator();

		// Register listener to forward contexts to Bukkit servers.
		ProxyServer server = getProxy();
		server.registerChannel(Constants.CHANNEL);
		server.getPluginManager().registerListener(this, new ContextSync(this, this.calculators));
	}

	@Override
	public void onDisable() {
		// Unregister contexts.
		for (var calculator : this.calculators) {
			this.manager.unregisterCalculator(calculator);
		}

		// Unregister listeners.
		ProxyServer server = getProxy();
		server.unregisterChannel(Constants.CHANNEL);
		server.getPluginManager().unregisterListeners(this);
	}

	private void registerCalculator(ContextCalculator<ProxiedPlayer> calculator) {
		this.manager.registerCalculator(calculator);
		this.calculators.add(calculator);
	}

	protected void registerClientTypeCalculator() {
		try {
			registerCalculator(new ClientTypeContextViaFloodgate());
			getLogger().info("Using Floodgate for client type detection.");
			return;
		} catch (Throwable t) {
		}

		try {
			registerCalculator(new ClientTypeContextViaGeyser());
			getLogger().info("Using Geyser for client type detection.");
			return;
		} catch (Throwable t) {
		}

		registerCalculator(new ClientTypeContextFallback());
		getLogger().info("Using fallback for client type detection.");
		getLogger().warning("All clients will appear as Java clients.");
	}

	protected void registerClientVersionCalculator() {
		try {
			VersionResolver resolver = new VersionResolver();
			registerCalculator(new ClientVersionContext(resolver));
		} catch (ReflectiveOperationException t) {
			getLogger().log(Level.WARNING, "Unable to generate client version map.", t);
		} catch (Throwable t) {
			getLogger().log(Level.WARNING, "Unable to register client version context.", t);
		}
	}

}
