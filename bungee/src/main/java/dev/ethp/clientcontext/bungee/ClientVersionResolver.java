package dev.ethp.clientcontext.bungee;

import dev.ethp.clientcontext.util.ClientVersion;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that acquires a list of supported client versions for the BungeeCord server.
 */
public class ClientVersionResolver {
	private final Map<Integer, MappedVersion> protocolToVersion;

	/**
	 * Create a new client version resolver.
	 *
	 * This attempts to create a map between client procotol versions and Minecraft game versions,
	 * using reflection to parse the {@link ProtocolConstants} enum.
	 *
	 * @throws ReflectiveOperationException If reflection fails.
	 */
	public ClientVersionResolver() throws ReflectiveOperationException, NumberFormatException {
		HashMap<Integer, MappedVersion> versions = new HashMap<>();

		// Parse the field names and values in ProtocolConstants.
		Class<?> constants = ProtocolConstants.class;
		for (var field : constants.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("MINECRAFT_")) {
				String[] versionParts = field.getName().substring(10).split("_");
				Integer versionProtocol = (Integer) field.get(null);

				int versionMajor = Integer.parseInt(versionParts[0]);
				int versionMinor = versionParts.length < 2 ? 0 : Integer.parseInt(versionParts[1]);
				int versionPatch = versionParts.length < 3 ? 0 : Integer.parseInt(versionParts[2]);

				versions.put(versionProtocol, new MappedVersion(versionProtocol, versionMajor, versionMinor, versionPatch));
			}
		}

		this.protocolToVersion = Collections.unmodifiableMap(versions);
	}

	/**
	 * Gets the version info from the Minecraft protocol version.
	 *
	 * @param protocolVersion The protocol version.
	 * @return The corresponding {@link ClientVersion} for the version, if supported. Null otherwise.
	 */
	public @Nullable ClientVersion fromProtocolVersion(int protocolVersion) {
		return this.protocolToVersion.get(protocolVersion);
	}

	public @Nullable ClientVersion fromPlayer(@NotNull ProxiedPlayer player) {
		return this.fromProtocolVersion(player.getPendingConnection().getVersion());
	}

	private static class MappedVersion extends ClientVersion {
		public final int protocolVersion;

		public MappedVersion(int protocolVersion, int major, int minor, int patch) {
			super(major, minor, patch);
			this.protocolVersion = protocolVersion;
		}
	}
}
