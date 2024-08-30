package org.karma.serialization;

import java.io.IOException;
import org.karma.serialization.QuickSerializer.RuntimeSerializer;
import static org.karma.serialization.QuickSerializer.getSerializer;

public class SerDataOutputStream {
	private final byte[] buffer;
	private final int bufferMaxSize;
	private int bufferPosition = 0;

	public SerDataOutputStream(int bufferSize) {
		this.bufferMaxSize = bufferSize;
		this.buffer = new byte[bufferSize];
	}
	private void checkBufferAvail(int bytesNeeded) {
		if (bufferPosition + bytesNeeded < bufferMaxSize) {
			return;
		}
		throw new RuntimeException();
	}
	private void writeNumber(int bytes, long number) {
		checkBufferAvail(bytes);
		for (int i = 0; i < bytes; i++) {
			buffer[bufferPosition++] = (byte) (0xff & (number >> (bytes - 1 - i) * 8));
		}
	}
	public void writeByte(byte number) {
		writeNumber(1, number);
	}
	public void writeShort(short number) {
		writeNumber(2, number);
	}
	public void writeInt(int number) {
		writeNumber(4, number);
	}
	public void writeLong(long number) {
		writeNumber(8, number);
	}
	public void writeChar(char character) {
		writeInt(character);
	}
	public void writeBoolean(boolean bool) throws IOException {
		writeByte((byte) (bool ? 1 : 0));
	}
	public void writeFloat(float number) throws IOException {
		checkBufferAvail(4);
		writeInt(Float.floatToIntBits(number));
	}
	public void writeDouble(double number) throws IOException {
		checkBufferAvail(8);
		writeLong(Double.doubleToLongBits(number));
	}
	public <T> void writeObject(T object) {
		var serializer = (RuntimeSerializer<T>) getSerializer(object.getClass());
		assert serializer != null;
		var subBuffer = new SerDataOutputStream(serializer.bytes());
		serializer.serializerInstance().serialize(subBuffer, object);
		var result = subBuffer.getFilledBuffer();
		var resultLength = result.length;

		writeShort(serializer.signature()); // Signature of type
		writeInt(resultLength); // Size of object

		for (byte b : result) writeByte(b);
	}
	private void reset() {
		bufferPosition = 0;
	}
	public byte[] getFilledBuffer() {
		byte[] filled = new byte[bufferPosition+1];
		System.arraycopy(buffer, 0, filled, 0, bufferPosition);
		return filled;
	}
	public void writeString(String string) {
		writeObject(string);
	}
}
