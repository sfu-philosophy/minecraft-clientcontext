package dev.ethp.clientcontext;

import net.luckperms.api.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A listener that sends a {@code clientcontext:forward} plugin message containing serialized contexts.
 * The serialized message format is:
 *
 * <pre>
 * [short] ITEM_COUNT
 * for ITEM_COUNT
 *     [string] CONTEXT_KEY
 *     [string] CONTEXT_VALUE
 * </pre>
 */
public class ContextData implements Context {
	private final @NotNull String key;
	private final @NotNull String value;

	public ContextData(@NotNull String key, @NotNull String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ContextData that = (ContextData) o;
		return Objects.equals(key, that.key) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}

	@Override
	public @NotNull String getKey() {
		return this.key;
	}

	@Override
	public @NotNull String getValue() {
		return this.value;
	}
}
