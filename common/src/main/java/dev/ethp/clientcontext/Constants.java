package dev.ethp.clientcontext;

public class Constants {
	public static final String CHANNEL = "client-context:sync";

	// List of LuckPerms contexts to register.

	public static final String LP_CONTEXT_CLIENT_TYPE = "client:type";
	public static final String LP_CONTEXT_CLIENT_VERSION = "client:version";
	public static final String LP_CONTEXT_CLIENT_DEVICE = "client:device";
	public static final String LP_CONTEXT_CLIENT_ARCHETYPE = "client:archetype";
	public static final String LP_CONTEXT_CLIENT_CONTROLS = "client:controls";

	// List of client types.

	/**
	 * An unknown client.
	 * <p>
	 * This is used when the client type cannot be determined.
	 */
	public static final String CLIENT_UNKNOWN = "java";

	/**
	 * A client directly connected to the server.
	 * <p>
	 * This is any client that is neither Floodgate nor Geyser.
	 */
	public static final String CLIENT_JAVA = "java";

	/**
	 * A client known by Floodgate.
	 * <p>
	 * Presumably, they're connected through Geyser <i>somewhere</i>,
	 * either directly on the same Bukkit server, or through Geyser-on-BungeeCord.
	 */
	public static final String CLIENT_FLOODGATE = "bedrock";

	/**
	 * A client connected through Geyser.
	 * <p>
	 * This is for Bedrock users connecting to a Geyser server.
	 */
	public static final String CLIENT_GEYSER = "bedrock";


	// List of client device archetypes.

	public static final String ARCHETYPE_MOBILE = "mobile";
	public static final String ARCHETYPE_CONSOLE = "console";
	public static final String ARCHETYPE_COMPUTER = "computer";

}
