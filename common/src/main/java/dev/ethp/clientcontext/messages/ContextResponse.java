package dev.ethp.clientcontext.messages;

import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * A plugin message with calculated context data.
 *
 * <p><b>Direction:</b><br/>
 * Proxy -> Server
 *
 * <p><b>Format:</b><br/>
 * <ul>
 *     <li>[String] UUID
 *     <li>[Short] Contexts [...]
 *     <ul>
 *         <li>[String] Key
 *         <li>[String] Value
 *     </ul>
 * </ul>
 */
public class ContextResponse {
	public static final String COMMAND = "context-response";
	public final UUID user;
	public final ContextSet contexts;

	public ContextResponse(@NotNull UUID user, @NotNull ContextSet contexts) {
		this.user = user;
		this.contexts = contexts;
	}

	public static void serialize(ContextResponse msg, DataOutputStream out) throws IOException {
		out.writeUTF(msg.user.toString());
		out.writeShort(msg.contexts.size());
		for (var context : msg.contexts) {
			out.writeUTF(context.getKey());
			out.writeUTF(context.getValue());
		}
	}

	public static ContextResponse deserialize(DataInputStream in) throws IOException {
		ImmutableContextSet.Builder contexts = ImmutableContextSet.builder();

		UUID user = UUID.fromString(in.readUTF());
		for (int i = 0, count = in.readShort(); i < count; i++) {
			contexts.add(in.readUTF(), in.readUTF());
		}

		return new ContextResponse(user, contexts.build());
	}
}
