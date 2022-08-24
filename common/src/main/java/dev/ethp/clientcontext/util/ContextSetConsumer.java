package dev.ethp.clientcontext.util;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.MutableContextSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * An implementation of a {@link ContextConsumer} that feeds all the data into a {@link ContextSet}.
 * This can be used for on-the-fly calculations of context sets without having to go through the LuckPerms API.
 */
public class ContextSetConsumer implements ContextConsumer {
	public final MutableContextSet contexts = MutableContextSet.create();

	@Override
	public void accept(@NonNull String key, @NonNull String value) {
		contexts.add(key, value);
	}

	@Override
	public void accept(@NonNull ContextSet contextSet) {
		contexts.addAll(contextSet);
	}

	/**
	 * Calculates {@link ContextSet} from zero or more {@link ContextCalculator} instances.
	 *
	 * @param target      The target player.
	 * @param calculators The context calculators.
	 * @param <Player>    The player class type.
	 * @return The calculated contexts.
	 */
	static public <Player> ContextSet from(@NotNull Player target, @NotNull Iterator<ContextCalculator<Player>> calculators) {
		var consumer = new ContextSetConsumer();

		while (calculators.hasNext()) {
			calculators.next().calculate(target, consumer);
		}

		return consumer.contexts.immutableCopy();
	}
	/**
	 * Calculates {@link ContextSet} from zero or more {@link ContextCalculator} instances.
	 *
	 * @param target      The target player.
	 * @param calculators The context calculators.
	 * @param <Player>    The player class type.
	 * @return The calculated contexts.
	 */
	static public <Player> ContextSet from(@NotNull Player target, @NotNull Iterable<ContextCalculator<Player>> calculators) {
		return from(target, calculators.iterator());
	}
}
