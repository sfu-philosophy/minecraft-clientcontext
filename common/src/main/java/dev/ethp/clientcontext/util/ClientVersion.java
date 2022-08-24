package dev.ethp.clientcontext.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A Minecraft version number.
 */
public class ClientVersion {
	public final int major;
	public final int minor;
	public final int patch;

	public ClientVersion(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	@Override
	public @NotNull String toString() {
		return this.toString(Format.FULL);
	}

	public @NotNull String toString(Format format) {
		ArrayList<String> parts = new ArrayList<>(3);

		if (format.addMajor) {
			parts.add(format.overrideMajor == null ? String.valueOf(this.major) : format.overrideMajor);
		}
		if (format.addMinor) {
			parts.add(format.overrideMinor == null ? String.valueOf(this.minor) : format.overrideMinor);
		}
		if (format.addPatch && (this.patch != 0 || format.overridePatch != null)) {
			parts.add(format.overridePatch == null ? String.valueOf(this.patch) : format.overridePatch);
		}

		return String.join(".", parts);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClientVersion)) return false;
		ClientVersion that = (ClientVersion) o;
		return major == that.major && minor == that.minor && patch == that.patch;
	}

	@Override
	public int hashCode() {
		return Objects.hash(major, minor, patch);
	}

	public enum Format {

		/**
		 * Format: {@code 1.2.3}
		 */
		FULL(true, true, true, null, null, null),

		/**
		 * Format: {@code 1}
		 */
		MAJOR(true, false, false, null, null, null),

		/**
		 * Format: {@code 1.2.x}
		 */
		MAJOR_MINOR_X(true, true, true, null, null, "X");

		final boolean addMajor;
		final boolean addMinor;
		final boolean addPatch;
		final String overrideMajor;
		final String overrideMinor;
		final String overridePatch;

		Format(boolean addMajor, boolean addMinor, boolean addPatch, String overrideMajor, String overrideMinor, String overridePatch) {
			this.addMajor = addMajor;
			this.addMinor = addMinor;
			this.addPatch = addPatch;
			this.overrideMajor = overrideMajor;
			this.overrideMinor = overrideMinor;
			this.overridePatch = overridePatch;
		}
	}
}
