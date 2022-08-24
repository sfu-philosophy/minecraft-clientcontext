package dev.ethp.clientcontext.contexts;

import dev.ethp.clientcontext.Constants;
import dev.ethp.clientcontext.PlatformAbstraction;
import dev.ethp.clientcontext.AbstractContextCalculator;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.jetbrains.annotations.NotNull;

/**
 * A fallback LuckPerms {@link ContextCalculator} that always specifies "java" as the client type.
 * This is used when Geyser or Floodgate fail to load.
 *
 * <p><b>Contexts:</b>
 * <code><pre>
 *     client:type=java
 *     client:type=bedrock
 * </pre></code>
 *
 * @param <Player> The player class type.
 */
public class FallbackCalculator<Player> extends AbstractContextCalculator<Player> {
	public final String CONTEXT = Constants.LP_CONTEXT_CLIENT_TYPE;

	public FallbackCalculator(@NotNull PlatformAbstraction<Player> platform) {
		super(platform);
	}

	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer contextConsumer) {
		contextConsumer.accept(CONTEXT, Constants.CLIENT_UNKNOWN);
	}

	@Override
	public @NotNull ContextSet estimatePotentialContexts() {
		ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

		builder.add(CONTEXT, Constants.CLIENT_UNKNOWN);

		return builder.build();
	}

}