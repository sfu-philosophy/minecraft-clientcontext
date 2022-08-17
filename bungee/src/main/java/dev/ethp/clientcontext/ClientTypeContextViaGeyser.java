package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.geysermc.api.Geyser;
import org.geysermc.api.session.Connection;
import org.jetbrains.annotations.NotNull;

/**
 * A LuckPerms {@link ContextCalculator} that checks if the player is connected to the server as a Java client, or
 * as a Bedrock client via GeyserMC. This requires Geyser to be loaded.
 */
public class ClientTypeContextViaGeyser implements ContextCalculator<ProxiedPlayer> {

	@Override
	public void calculate(ProxiedPlayer target, ContextConsumer contextConsumer) {
		Connection session = Geyser.api().connectionByUuid(target.getUniqueId());
		contextConsumer.accept("client-type", session == null ? "java" : "bedrock");
	}

	@Override
	public @NotNull ContextSet estimatePotentialContexts() {
		ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

		builder.add("client-type", "java");
		builder.add("client-type", "bedrock");

		return builder.build();
	}

}