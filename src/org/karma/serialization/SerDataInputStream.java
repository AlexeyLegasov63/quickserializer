package org.karma.serialization;

import org.karma.serialization.QuickSerializer.RuntimeSerializer;
import static org.karma.serialization.QuickSerializer.*;
import static java.lang.String.format;

public class SerDataInputStream {
	private final byte[] buffer;
	private final int bufferSize;
	private int bufferPosition = 0;

	public SerDataInputStream(byte[] bytes) {
		this.bufferSize = bytes.length;
		this.buffer = bytes;
	}

	/**
		Throws SerializerEndOfBufferException if there's not enough bytes
	 */
	private void checkBufferAvail(int bytesNeeded) {
		var askedBytes = bufferPosition + bytesNeeded;
		if (askedBytes < bufferSize) {
			return;
		}
		throw new SerializerEndOfBufferException(format("Reached the end of buffer. Buffer size: %s, asked: %s", bufferSize, askedBytes));
	}

	/**
		 Returns a number with a special number of bytes
	 */
	private long readNumber(int bytes) {
		checkBufferAvail(bytes); // Ensure that there's enough bytes
		var number = (long) (buffer[bufferPosition++] & 0xff) << (bytes - 1) * 8;
		for (int i = 1; i < bytes; i++) {
			number |= (long) (buffer[bufferPosition++] & 0xff) << (bytes - 1 - i) * 8;
		}
		return number;
	}

	/**
		Just skip bytes by adding them to current position
	 */
	private void skipBytes(int bytes) {
		checkBufferAvail(bytes); // Ensure that there's enough bytes
		bufferPosition += bytes;
	}

	/**
	    Returns the available buffer space
	 */
	public int available() {
		return bufferSize - bufferPosition - 1;
	}

	/**
	    Returns <b>true</b> if available() doesn't equals to 0
	 */
	public boolean hasAvailable() {
		return available() > 0;
	}

	/**
	    Skip object bytes (at least 6)
	 */
	public void skipObject() {
		skipShort();
		var objectSize = readInt();
		if (objectSize < 0) // A negative size means a null instance, and this doesn't have data. So we just return
			return;
		skipBytes(objectSize);
	}

	/**
	    Read 1 byte
	 */
	public byte readByte() {
		return (byte) readNumber(1);
	}

	/**
	    Skip 1 byte
	 */
	public void skipByte() {
		skipBytes(1);
	}

	/**
	    Read 2 bytes
	 */
	public short readShort() {
		return (short) readNumber(2);
	}

	/**
	    Skip 2 bytes
	 */
	public void skipShort() {
		skipBytes(2);
	}

	/**
		 Read 4 bytes
	 */
	public int readInt() {
		return (int) readNumber(4);
	}

	/**
	    Skip 4 bytes
	 */
	public void skipInt() {
		skipBytes(4);
	}

	/**
	    Read 8 bytes
	 */
	public long readLong() {
		return readNumber(8);
	}

	/**
	    Skip 8 bytes
	 */
	public void skipLong() {
		skipBytes(8);
	}

	/**
	    Read 4 bytes
	 */
	public char readChar() {
		return (char) readInt();
	}

	/**
		 Skip 4 bytes
	 */
	public void skipChar() {
		skipInt();
	}

	/**
	    Read 1 byte. Returns true if this byte greater or equals to 1
	 */
	public boolean readBoolean() {
		return readByte() >= 1;
	}

	/**
	    Skip 1 byte
	 */
	public void skipBoolean() {
		skipByte();
	}

	/**
	    Read 4 bytes
	 */
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	/**
	    Skip 4 bytes
	 */
	public void skipFloat() {
		skipInt();
	}

	/**
	    Read 8 bytes
	 */
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	/**
	    Skip 8 bytes
	 */
	public void skipDouble() {
		skipLong();
	}

	/**
	    Requires at least 6 bytes

	    Load an object by Class
	 */
	public <T> T readObject(Class<T> objectClass) {
		skipBytes(2); // Skip object class signature
		return readObject0(getSerializer(objectClass), objectClass.getSimpleName());
	}

	/**
	    Requires at least 6 bytes

	    Load an object by Signature
	 */
	public <T> T readObject() {
		var objectSignature = readShort(); // Read the object class signature
		return readObject0(getSerializer(objectSignature), format("0x%s", Integer.toHexString(objectSignature)));
	}
	private <T> T readObject0(RuntimeSerializer<T> serializer, String source) {
		if (serializer == null) {
			throw new SerializerObjectUnknownException(format("Unknown serializer: %s", source));
		}
		var objectSize = readInt(); // Size of object data
		if (objectSize < 0) {
			return null; // A negative size means a null instance
		}
		var objectData = new byte[objectSize];
		for (int i = 0; i < objectSize; i++) {
			objectData[i] = readByte();
		}
		var subBuffer = new SerDataInputStream(objectData); // Create a sub-buffer with object data
		try {
			return serializer.serializerInstance().deserialize(subBuffer);
		} catch (Throwable t) {
			throw new SerializerObjectLoadException(format("Failed to load object: %s", serializer.objectClass().getSimpleName()), t);
		}
	}

	/**
	    Requires at least 6 bytes. Returns a string if there's a serialized string object
	    Or throws an exception
	 */
	public String readString() {
		return readObject();
	}

	/**
	    Skip 6 bytes
	 */
	public void skipString() {
		skipObject();
	}
}
