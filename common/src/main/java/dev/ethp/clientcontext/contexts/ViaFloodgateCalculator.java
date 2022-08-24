package dev.ethp.clientcontext.contexts;

import dev.ethp.clientcontext.Constants;
import dev.ethp.clientcontext.PlatformAbstraction;
import dev.ethp.clientcontext.AbstractContextCalculator;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.util.DeviceOs;
import org.geysermc.floodgate.util.InputMode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A LuckPerms {@link ContextCalculator} that checks if the player is connected to the server as a Java client, or
 * as a Bedrock client via GeyserMC. This requires Floodgate to be loaded.
 *
 *
 * <p><b>Contexts:</b>
 * <code><pre>
 *     client:type=java
 *     client:type=bedrock
 *     client:archetype=computer
 *     client:archetype=mobile
 *     client:archetype=console
 *     client:device=...
 *     client:controls=keyboard_mouse
 *     client:controls=controller
 *     client:controls=touch
 * </pre></code>
 *
 * <p><b>Notes:</b>
 * <ul>
 *     <li> When a Java client is connected, it is assumed that the device archetype is a {@code computer}.
 * </ul>
 *
 * @param <Player> The player class type.
 */
public class ViaFloodgateCalculator<Player> extends AbstractContextCalculator<Player> {
	protected final @NotNull FloodgateApi floodgate;
	protected final @NotNull Map<DeviceOs, String> archetypes;

	public ViaFloodgateCalculator(@NotNull PlatformAbstraction<Player> platform) {
		super(platform);
		this.floodgate = FloodgateApi.getInstance();

		this.archetypes = Map.of(
				DeviceOs.IOS, Constants.ARCHETYPE_MOBILE,
				DeviceOs.WINDOWS_PHONE, Constants.ARCHETYPE_MOBILE,
				DeviceOs.GOOGLE, Constants.ARCHETYPE_MOBILE,
				DeviceOs.AMAZON, Constants.ARCHETYPE_MOBILE,

				DeviceOs.NX, Constants.ARCHETYPE_CONSOLE,
				DeviceOs.XBOX, Constants.ARCHETYPE_CONSOLE,
				DeviceOs.PS4, Constants.ARCHETYPE_CONSOLE
		);
	}

	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer contextConsumer) {
		UUID targetId = platform.getUniqueId(target);
		if (!floodgate.isFloodgatePlayer(targetId)) {
			contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_TYPE, Constants.CLIENT_JAVA);
			contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_ARCHETYPE, Constants.ARCHETYPE_COMPUTER);
			contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_CONTROLS, InputMode.KEYBOARD_MOUSE.name().toLowerCase());
			return;
		}

		FloodgatePlayer info = floodgate.getPlayer(platform.getUniqueId(target));
		contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_TYPE, Constants.CLIENT_FLOODGATE);
		contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_ARCHETYPE, archetypes.getOrDefault(info.getDeviceOs(), Constants.ARCHETYPE_COMPUTER));
		contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_DEVICE, info.getDeviceOs().name().toLowerCase());
		contextConsumer.accept(Constants.LP_CONTEXT_CLIENT_CONTROLS, info.getInputMode().name().toLowerCase());
	}

	@Override
	public @NotNull ContextSet estimatePotentialContexts() {
		ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

		// Client types.
		builder.add(Constants.LP_CONTEXT_CLIENT_TYPE, Constants.CLIENT_JAVA);
		builder.add(Constants.LP_CONTEXT_CLIENT_TYPE, Constants.CLIENT_FLOODGATE);

		// Client archetypes.
		builder.add(Constants.LP_CONTEXT_CLIENT_DEVICE, Constants.ARCHETYPE_COMPUTER);
		builder.add(Constants.LP_CONTEXT_CLIENT_DEVICE, Constants.ARCHETYPE_CONSOLE);
		builder.add(Constants.LP_CONTEXT_CLIENT_DEVICE, Constants.ARCHETYPE_MOBILE);

		// Client devices.
		builder.add(Constants.LP_CONTEXT_CLIENT_DEVICE, "java");
		for (var device : DeviceOs.values()) {
			builder.add(Constants.LP_CONTEXT_CLIENT_DEVICE, device.name().toLowerCase());
		}

		// Client controls.
		for (var mode : InputMode.values()) {
			builder.add(Constants.LP_CONTEXT_CLIENT_CONTROLS, mode.name().toLowerCase());
		}

		return builder.build();
	}

}