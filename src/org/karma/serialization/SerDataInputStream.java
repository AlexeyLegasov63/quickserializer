package org.karma.serialization;

import org.karma.serialization.QuickSerializer.RuntimeSerializer;
import static org.karma.serialization.QuickSerializer.*;

public class SerDataInputStream {
	private final byte[] buffer;
	private final int bufferSize;
	private int bufferPosition = 0;

	public SerDataInputStream(byte[] bytes) {
		this.bufferSize = bytes.length;
		this.buffer = bytes;
	}

	private void checkBufferAvail(int bytesNeeded) {
		if (bufferPosition + bytesNeeded < bufferSize) {
			return;
		}
		throw new RuntimeException();
	}

	private void skipBytes(int bytes) {
		checkBufferAvail(bytes);
		bufferPosition += bytes;
	}

	public byte readByte() {
		checkBufferAvail(1);
		return buffer[bufferPosition++];
	}
	public short readShort() {
		checkBufferAvail(2);
		return (short)
				(((short) (buffer[bufferPosition++] & 0xff) << 8) |
						((short) (buffer[bufferPosition++] & 0xff)));
	}
	public int readInt() {
		checkBufferAvail(4);
		return (((buffer[bufferPosition++] & 0xff) << 24) |
				((buffer[bufferPosition++] & 0xff) << 16) |
				((buffer[bufferPosition++] & 0xff) << 8) |
				((buffer[bufferPosition++] & 0xff)));
	}
	public long readLong() {
		checkBufferAvail(8);
		return (((long) (buffer[bufferPosition++] & 0xff) << 56) |
				((long) (buffer[bufferPosition++] & 0xff) << 48) |
				((long) (buffer[bufferPosition++] & 0xff) << 40) |
				((long) (buffer[bufferPosition++] & 0xff) << 32) |
				((long) (buffer[bufferPosition++] & 0xff) << 24) |
				((long) (buffer[bufferPosition++] & 0xff) << 16) |
				((long) (buffer[bufferPosition++] & 0xff) << 8) |
				((long) (buffer[bufferPosition++] & 0xff)));
	}
	public char readChar() {
		return (char) readInt();
	}
	public boolean readBoolean() {
		return readByte() == 1;
	}
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}
	public <T> T readObject(Class<T> objectClass) {
		skipBytes(2);
		var objectSize = readInt();
		var objectData = new byte[objectSize];
		for (int i = 0; i < objectSize; i++)
			objectData[i] = readByte();
		var serializer = (RuntimeSerializer<T>) getSerializer(objectClass);
		assert serializer != null;
		var subBuffer = new SerDataInputStream(objectData);
		return (T) serializer.serializerInstance().deserialize(subBuffer);
	}
	public <T> T readObject() {
		var objectSignature = readShort();
		var objectSize = readInt();
		var objectData = new byte[objectSize];
		for (int i = 0; i < objectSize; i++)
			objectData[i] = readByte();
		var serializer = (RuntimeSerializer<T>) getSerializer(objectSignature);
		assert serializer != null;
		var subBuffer = new SerDataInputStream(objectData);
		return (T) serializer.serializerInstance().deserialize(subBuffer);
	}
	public String readString() {
		return readObject();
	}
}
