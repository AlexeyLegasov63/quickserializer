package org.karma.serialization;

/**
 *  Thread-Safe Serialization Input Reader
 */
public class ConcurrentSerializationInput extends SerializationInput {

	public ConcurrentSerializationInput(byte[] bytes) {
		super(bytes);
	}

	/**
	    Returns the available buffer space
	 */
	@Override
	public synchronized int available() {
		return super.available();
	}

	/**
	    Returns <b>true</b> if available() doesn't equals to 0
	 */
	@Override
	public synchronized boolean hasAvailable() {
		return super.hasAvailable();
	}

	/**
	    Skip object bytes (at least 6)
	 */
	@Override
	public synchronized void skipObject() {
		super.skipObject();
	}

	/**
	    Read 1 byte
	 */
	@Override
	public synchronized byte readByte() {
		return super.readByte();
	}

	/**
	    Skip 1 byte
	 */
	@Override
	public synchronized void skipByte() {
		super.skipByte();
	}

	/**
	    Read 2 bytes
	 */
	@Override
	public synchronized short readShort() {
		return super.readShort();
	}

	/**
	    Skip 2 bytes
	 */
	@Override
	public synchronized void skipShort() {
		super.skipShort();
	}

	/**
		 Read 4 bytes
	 */
	@Override
	public synchronized int readInt() {
		return super.readInt();
	}

	/**
	    Skip 4 bytes
	 */
	@Override
	public synchronized void skipInt() {
		super.skipInt();
	}

	/**
	    Read 8 bytes
	 */
	@Override
	public synchronized long readLong() {
		return super.readLong();
	}

	/**
	    Skip 8 bytes
	 */
	@Override
	public synchronized void skipLong() {
		super.skipLong();
	}

	/**
	    Read 4 bytes
	 */
	@Override
	public synchronized char readChar() {
		return super.readChar();
	}

	/**
		 Skip 4 bytes
	 */
	@Override
	public synchronized void skipChar() {
		super.skipChar();
	}

	/**
	    Read 1 byte. Returns true if this byte greater or equals to 1
	 */
	@Override
	public synchronized boolean readBoolean() {
		return super.readBoolean();
	}

	/**
	    Skip 1 byte
	 */
	@Override
	public synchronized void skipBoolean() {
		super.skipBoolean();
	}

	/**
	    Read 4 bytes
	 */
	@Override
	public synchronized float readFloat() {
		return super.readFloat();
	}

	/**
	    Skip 4 bytes
	 */
	@Override
	public synchronized void skipFloat() {
		super.skipFloat();
	}

	/**
	    Read 8 bytes
	 */
	@Override
	public synchronized double readDouble() {
		return super.readDouble();
	}

	/**
	    Skip 8 bytes
	 */
	@Override
	public synchronized void skipDouble() {
		super.skipDouble();
	}

	/**
	    Requires at least 6 bytes

	    Load an object by Class
	 */
	@Override
	public synchronized <T> T readObject(Class<T> objectClass) {
		return super.readObject(objectClass);
	}

	/**
	    Requires at least 6 bytes

	    Load an object by Signature
	 */
	@Override
	public synchronized <T> T readObject() {
		return super.readObject();
	}

	/**
	    Requires at least 6 bytes. Returns a string if there's a serialized string object
	    Or throws an exception
	 */
	@Override
	public synchronized String readString() {
		return super.readString();
	}

	/**
	    Skip 6 bytes
	 */
	@Override
	public synchronized void skipString() {
		super.skipString();
	}
}
