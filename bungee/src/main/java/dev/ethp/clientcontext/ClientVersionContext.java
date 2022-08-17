package dev.ethp.clientcontext;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * A LuckPerms {@link ContextCalculator} that sets a context for the client version.
 */
public class ClientVersionContext implements ContextCalculator<ProxiedPlayer> {
	private final VersionResolver resolver;

	public ClientVersionContext(@NotNull VersionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void calculate(ProxiedPlayer target, @NotNull ContextConsumer contextConsumer) {
		int protocolVersion = target.getPendingConnection().getVersion();
		VersionResolver.Info version = this.resolver.fromProtocolVersion(protocolVersion);
		if (version == null) {
			contextConsumer.accept("client-version", "unknown");
			return;
		}

		contextConsumer.accept("client-version", version.exact);
		contextConsumer.accept("client-version", version.rough);
	}

}