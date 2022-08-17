package dev.ethp.clientcontext;

import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that acquires a list of supported client versions for the Bungeecord server.
 */
public class VersionResolver {

	/**
	 * Version information.
	 */
	public static class Info {

		/**
		 * The protocol version number.
		 */
		public final int protocol;

		/**
		 * The rough version number.
		 * {@code 1.2}, {@code 1.2.1}, etc.
		 */
		public final String rough;

		/**
		 * The exact version number.
		 * {@code 1.2.x}
		 */
		public final String exact;

		public Info(int protocol, String exactVersion) {
			this.protocol = protocol;
			this.exact = exactVersion;

			// Parse the exact version into a MAJOR.MINOR.x string.
			String[] parts = this.exact.split("\\.");
			this.rough = parts[0] + "." + parts[1] + ".x";
		}
	}

	private final Map<Integer, Info> protocolToVersion;

	/**
	 * Create a new ProtocolResolver.
	 *
	 * @throws ReflectiveOperationException If reflection fails.
	 */
	public VersionResolver() throws ReflectiveOperationException {
		HashMap<Integer, Info> versions = new HashMap<>();

		// Parse the field names and values in ProtocolConstants.
		Class<?> constants = ProtocolConstants.class;
		for (var field : constants.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("MINECRAFT_")) {
				String versionName = field.getName().substring(10).replace("_", ".");
				Integer versionProtocol = (Integer) field.get(null);
				versions.put(versionProtocol, new Info(versionProtocol, versionName));
			}
		}

		this.protocolToVersion = Collections.unmodifiableMap(versions);
	}

	/**
	 * Gets the version info from the Minecraft protocol version.
	 *
	 * @param protocolVersion The protocol version.
	 * @return The corresponding {@link Info} for the version, if supported. Null otherwise.
	 */
	public @Nullable Info fromProtocolVersion(int protocolVersion) {
		return this.protocolToVersion.get(protocolVersion);
	}

}
