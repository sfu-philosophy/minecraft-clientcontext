package dev.ethp.clientcontext.bukkit;

import java.lang.reflect.Field;

/**
 * Utilities for working inside a BungeeCord network.
 */
public class BungeeUtil {

	/**
	 * Check to see if the server is running under BungeeCord.
	 *
	 * @return True if the spigot.yml file is configured to run under under BungeeCord.
	 */
	public static boolean isRunningUnderBungee() {
		// Try finding out directly through the class that contains the config:
		// https://hub.spigotmc.org/stash/projects/SPIGOT/repos/spigot/browse/CraftBukkit-Patches/0030-BungeeCord-Support.patch?until=450dcaa86efd759674bbdeae0f6a37c97977618e&untilPath=CraftBukkit-Patches%2F0030-BungeeCord-Support.patch#215
		try {
			Class<?> config = BungeeUtil.class.getClassLoader().loadClass("org.spigotmc.SpigotConfig");
			Field bungee = config.getField("bungee");
			return (Boolean) bungee.get(null);
		} catch (ReflectiveOperationException ignored) {
		}

		// Not running a Spigot server?
		return false;
	}

}
