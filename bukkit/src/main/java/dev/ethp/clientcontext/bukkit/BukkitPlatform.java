package dev.ethp.clientcontext.bukkit;

import dev.ethp.clientcontext.PlatformAbstraction;
import dev.ethp.clientcontext.util.ClientVersion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of platform abstraction for a Bukkit/Spigot/Paper server.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class BukkitPlatform implements PlatformAbstraction<Player> {
	private final JavaPlugin plugin;
	private ClientVersion version;

	public BukkitPlatform(@NotNull JavaPlugin plugin) {
		this.plugin = plugin;

		// Parse the server version string.
		String[] versionParts = this.plugin.getServer().getBukkitVersion().split("[-_]")[0].split("\\.");
		int versionMajor = Integer.parseInt(versionParts[0]);
		int versionMinor = versionParts.length < 2 ? 0 : Integer.parseInt(versionParts[1]);
		int versionPatch = versionParts.length < 3 ? 0 : Integer.parseInt(versionParts[2]);
		this.version = new ClientVersion(versionMajor, versionMinor, versionPatch);
	}

	@Override
	public @NotNull UUID getUniqueId(@NotNull Player player) {
		return player.getUniqueId();
	}

	@Override
	public @NotNull Optional<ClientVersion> getPlayerClientVersion(@NotNull Player player) {
		return Optional.of(this.version);
	}
}
