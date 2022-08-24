package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract base class for calculating LuckPerms contexts across different server implementations.
 *
 * @param <Player> The player class type.
 */
public abstract class AbstractContextCalculator<Player> implements ContextCalculator<Player> {

	protected final @NotNull PlatformAbstraction<Player> platform;

	public AbstractContextCalculator(@NotNull PlatformAbstraction<Player> platform) {
		this.platform = platform;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void calculate(@NotNull Player target, @NotNull ContextConsumer consumer);

}