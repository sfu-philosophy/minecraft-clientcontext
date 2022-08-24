package dev.ethp.clientcontext;

import dev.ethp.clientcontext.contexts.FallbackCalculator;
import dev.ethp.clientcontext.contexts.VersionCalculator;
import dev.ethp.clientcontext.contexts.ViaFloodgateCalculator;
import dev.ethp.clientcontext.util.ContextSetConsumer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.logging.Logger;

/**
 * A class that loads all the contexts that are available for all supported platforms.
 *
 * @param <Player> The player class type.
 */
public final class CommonPlugin<Player> {
	private final @NotNull HashSet<ContextCalculator<Player>> calculators;
	private final @NotNull PlatformAbstraction<Player> platform;
	private final @NotNull Logger logger;
	private final @NotNull LuckPerms luckperms;

	public CommonPlugin(@NotNull Logger logger, @NotNull PlatformAbstraction<Player> platform) {
		this.luckperms = LuckPermsProvider.get();
		this.calculators = new HashSet<>();
		this.platform = platform;
		this.logger = logger;
	}

	public void unregister() {
		logger.fine("Unregistering context calculators.");

		var cm = this.luckperms.getContextManager();
		for (var calculator : this.calculators) {
			cm.unregisterCalculator(calculator);
		}
		this.calculators.clear();
	}

	public void registerDefaults() {
		logger.fine("Registering context calculators.");

		register(new VersionCalculator<>(platform));
		registerClientInfo();
	}

	private void registerClientInfo() {
		try {
			register(new ViaFloodgateCalculator<>(platform));
			logger.info("Using Floodgate for client detection.");
			return;
		} catch (NoClassDefFoundError ignored) {
		}

		register(new FallbackCalculator<>(platform));
		logger.warning("Using fallback for client detection.");
		logger.warning("Floodgate is preferred. No client info is available under this mode.");
	}

	public void register(ContextCalculator<Player> calculator) {
		this.calculators.add(calculator);
		this.luckperms.getContextManager().registerCalculator(calculator);
	}

	public @NotNull ContextSet calculate(@NotNull Player player) {
		return ContextSetConsumer.from(player, this.calculators.iterator());
	}

}
