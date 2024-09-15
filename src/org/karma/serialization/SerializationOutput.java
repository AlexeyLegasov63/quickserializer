package org.karma.serialization;

import org.karma.serialization.QuickSerializer.RuntimeSerializer;

import static java.lang.String.format;
import static org.karma.serialization.QuickSerializer.getSerializer;

@SuppressWarnings(value = "unchecked")
public class SerializationOutput {
	private final byte[] buffer;
	private final int bufferMaxSize;
	private int bufferPosition = 0;

	/**
		 @param bufferSize - max size of buffer. (in bytes)
	 */
	SerializationOutput(int bufferSize) {
		this.bufferMaxSize = bufferSize;
		this.buffer = new byte[bufferSize];
	}
	/**
	    Throws SerializerEndOfBufferException if there's not enough bytes
	 */
	private void checkBufferAvail(int bytesNeeded) {
		var askedBytes = bufferPosition + bytesNeeded;
		if (askedBytes < bufferMaxSize) {
			return;
		}
		throw new SerializerEndOfBufferException(format("Reached the end of buffer. Buffer size: %s, asked: %s", bufferMaxSize, askedBytes));
	}
	/**
	    Returns a number with a special number of bytes
	 */
	private void writeNumber(int bytes, long number) {
		checkBufferAvail(bytes); // Ensure that there's enough bytes
		for (int i = 0; i < bytes; i++) {
			buffer[bufferPosition++] = (byte) (0xff & (number >> (bytes - 1 - i) * 8));
		}
	}
	/**
	    Requires 1 byte
	 */
	public void writeByte(byte number) {
		writeNumber(1, number);
	}
	/**
	    Requires 2 bytes
	 */
	public void writeShort(short number) {
		writeNumber(2, number);
	}
	/**
	    Requires 4 bytes
	 */
	public void writeInt(int number) {
		writeNumber(4, number);
	}
	/**
		 Requires 8 bytes
	 */
	public void writeLong(long number) {
		writeNumber(8, number);
	}
	/**
	    Requires 4 bytes
	 */
	public void writeChar(char character) {
		writeInt(character);
	}
	/**
		 Requires 1 byte
	 */
	public void writeBoolean(boolean bool) {
		writeByte((byte) (bool ? 1 : 0));
	}
	/**
	    Requires 4 bytes
	 */
	public void writeFloat(float number) {
		checkBufferAvail(4);
		writeInt(Float.floatToIntBits(number));
	}
	/**
		Requires 8 bytes
	 */
	public void writeDouble(double number) {
		checkBufferAvail(8);
		writeLong(Double.doubleToLongBits(number));
	}
	/**
	    Requires at least 10 bytes
	 */
	public void writeString(String string) {
		writeObject(string);
	}
	/**
	    Requires 6 bytes

	    Write a null instance of Class<T>
	 */
	public <T> void writeNull(Class<T> objectClass) {
		var serializer = (RuntimeSerializer<T>) getSerializer(objectClass); // Get object serializer

		writeShort(serializer.signature()); // Signature of the type
		writeInt(-1); // Negative size means Null
	}
	/**
	    Requires at least 6 bytes
	 */
	public <T> void writeObject(T object) {
		var serializer = (RuntimeSerializer<T>) getSerializer(object.getClass());
		assert serializer != null;

		writeShort(serializer.signature()); // Signature of the type

		var subBuffer = new SerializationOutput(serializer.bytes()); // Create sub-buffer with SerializerObject.bytes() size

		try {
			serializer.serializerInstance().serialize(subBuffer, object); // We don't use current buffer due to safety. So we create a new one
		} catch (Throwable t) {
			writeInt(-1); // A negative size means a null instance
			throw new SerializerObjectWriteException(format("Failed to write object. Written null instead of %s", serializer.objectClass().getSimpleName()), t);
		}

		var result = subBuffer.getBytes(); // Get the used space of subBuffer
		var resultLength = result.length;

		writeInt(resultLength); // Size of object

		for (int i = 0; i < resultLength; i++) {
			writeByte(result[i]); // Rewrite byte from subBuffer to current
		}
	}
	/**
		 Reset buffer position
	 */
	private void reset() {
		bufferPosition = 0;
	}
	/**
		 Get the used space
	 */
	public byte[] getBytes() {
		byte[] filled = new byte[bufferPosition+1];
		System.arraycopy(buffer, 0, filled, 0, bufferPosition);
		return filled;
	}
}
