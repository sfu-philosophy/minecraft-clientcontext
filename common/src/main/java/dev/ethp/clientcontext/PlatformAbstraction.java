package dev.ethp.clientcontext;

import dev.ethp.clientcontext.util.ClientVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * An interface for abstracting over different "server" platforms.
 * <p>
 * This allows common code to be reused between {@code Bukkit} and {@code BungeeCord}.
 *
 * @param <Player> The player class type.
 */
public interface PlatformAbstraction<Player> {

	/**
	 * Get the UUID of a player.
	 *
	 * @param player The player.
	 * @return The player's UUID.
	 */
	@NotNull UUID getUniqueId(@NotNull Player player);

	/**
	 * Get the client version of a player.
	 *
	 * @param player The player.
	 * @return The player's client version.
	 */
	@NotNull Optional<ClientVersion> getPlayerClientVersion(@NotNull Player player);

}
