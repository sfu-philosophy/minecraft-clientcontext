package dev.ethp.clientcontext.bungee;

import dev.ethp.clientcontext.PlatformAbstraction;
import dev.ethp.clientcontext.util.ClientVersion;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of platform abstraction for a BungeeCord server.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class BungeePlatform implements PlatformAbstraction<ProxiedPlayer> {
	private final ClientVersionResolver versionResolver;
	private final Plugin plugin;

	public BungeePlatform(@NotNull Plugin plugin, @Nullable ClientVersionResolver versionResolver) {
		this.plugin = plugin;
		this.versionResolver = versionResolver;
	}

	@Override
	public @NotNull UUID getUniqueId(@NotNull ProxiedPlayer player) {
		return player.getUniqueId();
	}

	@Override
	public @NotNull Optional<ClientVersion> getPlayerClientVersion(@NotNull ProxiedPlayer player) {
		if (this.versionResolver == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(versionResolver.fromPlayer(player));
	}
}
