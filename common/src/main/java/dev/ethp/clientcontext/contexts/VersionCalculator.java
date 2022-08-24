package dev.ethp.clientcontext.contexts;

import dev.ethp.clientcontext.Constants;
import dev.ethp.clientcontext.PlatformAbstraction;
import dev.ethp.clientcontext.AbstractContextCalculator;
import dev.ethp.clientcontext.util.ClientVersion;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * A LuckPerms {@link ContextCalculator} that sets a context for the client version.
 *
 * <p><b>Contexts:</b>
 * <code><pre>
 *     client:version=1.2.3
 *     client:version=1.2.x
 * </pre></code>
 *
 * @param <Player> The player class type.
 */
public class VersionCalculator<Player> extends AbstractContextCalculator<Player> {
	public final String CONTEXT = Constants.LP_CONTEXT_CLIENT_VERSION;

	public VersionCalculator(@NotNull PlatformAbstraction<Player> platform) {
		super(platform);
	}

	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer contextConsumer) {
		ClientVersion version = platform.getPlayerClientVersion(target).orElse(null);
		if (version != null) {
			contextConsumer.accept(CONTEXT, version.toString(ClientVersion.Format.FULL));
			contextConsumer.accept(CONTEXT, version.toString(ClientVersion.Format.MAJOR_MINOR_X));
		}

	}

}