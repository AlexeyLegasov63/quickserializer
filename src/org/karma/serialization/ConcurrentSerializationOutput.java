package org.karma.serialization;

public class ConcurrentSerializationOutput extends SerializationOutput {
	/**
	 * @param bufferSize - max size of buffer. (in bytes)
	 */
	public ConcurrentSerializationOutput(int bufferSize) {
		super(bufferSize);
	}

	/**
	 * Requires 1 byte
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeByte(byte number) {
		super.writeByte(number);
	}

	/**
	 * Requires 2 bytes
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeShort(short number) {
		super.writeShort(number);
	}

	/**
	 * Requires 4 bytes
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeInt(int number) {
		super.writeInt(number);
	}

	/**
	 * Requires 8 bytes
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeLong(long number) {
		super.writeLong(number);
	}

	/**
	 * Requires 4 bytes
	 *
	 * @param character
	 */
	@Override
	public synchronized void writeChar(char character) {
		super.writeChar(character);
	}

	/**
	 * Requires 1 byte
	 *
	 * @param bool
	 */
	@Override
	public synchronized void writeBoolean(boolean bool) {
		super.writeBoolean(bool);
	}

	/**
	 * Requires 4 bytes
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeFloat(float number) {
		super.writeFloat(number);
	}

	/**
	 * Requires 8 bytes
	 *
	 * @param number
	 */
	@Override
	public synchronized void writeDouble(double number) {
		super.writeDouble(number);
	}

	/**
	 * Requires at least 10 bytes
	 *
	 * @param string
	 */
	@Override
	public synchronized void writeString(String string) {
		super.writeString(string);
	}

	/**
	 * Requires 6 bytes
	 * <p>
	 * Write a null instance of Class<T>
	 *
	 * @param objectClass
	 */
	@Override
	public synchronized <T> void writeNull(Class<T> objectClass) {
		super.writeNull(objectClass);
	}

	/**
	 * Requires at least 6 bytes
	 *
	 * @param object
	 */
	@Override
	public synchronized <T> void writeObject(T object) {
		super.writeObject(object);
	}

	/**
	 * Get the used space
	 */
	@Override
	public synchronized byte[] getBytes() {
		return super.getBytes();
	}
}
