package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A fallback LuckPerms {@link ContextCalculator} that always specifies "java" as the client type.
 * This is used when Geyser fails to load.
 */
public class ClientTypeContextFallback implements ContextCalculator<ProxiedPlayer> {

	@Override
	public void calculate(ProxiedPlayer target, ContextConsumer contextConsumer) {
		contextConsumer.accept("client-type", "java");
	}

	@Override
	public @NotNull ContextSet estimatePotentialContexts() {
		ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

		builder.add("client-type", "java");

		return builder.build();
	}

}