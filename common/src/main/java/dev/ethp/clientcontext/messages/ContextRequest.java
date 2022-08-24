package dev.ethp.clientcontext.messages;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * A plugin message requesting calculated context data.
 *
 * <p><b>Direction:</b><br/>
 * Server -> Proxy
 *
 * <p><b>Format:</b><br/>
 * <ul>
 *     <li>[String] UUID
 * </ul>
 */
public class ContextRequest {
	public static final String COMMAND = "context-request";
	public final UUID user;

	public ContextRequest(@NotNull UUID user) {
		this.user = user;
	}

	public static void serialize(ContextRequest msg, DataOutputStream out) throws IOException {
		out.writeUTF(msg.user.toString());
	}

	public static ContextRequest deserialize(DataInputStream in) throws IOException {
		String uuid = in.readUTF();
		return new ContextRequest(UUID.fromString(uuid));
	}
}
