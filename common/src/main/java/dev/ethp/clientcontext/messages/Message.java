package dev.ethp.clientcontext.messages;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class Message {

	/**
	 * Generate a message.
	 *
	 * @param type    The message serialization function.
	 * @param command The message command.
	 * @param data    The message data.
	 * @param <T>     The message type.
	 * @return The serialized message bytes.
	 */
	public static <T> byte[] gen(Serializer<T> type, String command, T data) {
		try {
			var buf = new ByteArrayOutputStream();
			var out = new DataOutputStream(buf);

			out.writeUTF(command.toLowerCase());
			type.serialize(data, out);

			out.close();
			return buf.toByteArray();
		} catch (IOException ex) {
			throw new RuntimeException("WTF? This should not happen.", ex);
		}
	}

	public interface Serializer<T> {
		void serialize(T msg, DataOutputStream out) throws IOException;
	}

}
