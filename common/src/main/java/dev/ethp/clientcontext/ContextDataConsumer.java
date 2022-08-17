package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that implements {@link ContextConsumer} to get the calculated data from a LuckPerms {@link ContextCalculator}.
 */
class ContextDataConsumer implements ContextConsumer {
	public final Set<ContextData> contexts = new HashSet<>();

	@Override
	public void accept(@NotNull String key, @NotNull String value) {
		contexts.add(new ContextData(key, value));
	}
}
