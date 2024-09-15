package examples;

import org.karma.serialization.*;

public class FirstExample {

	/**
	    <h1>First example:</h1>

	    Basic QuickSerializer using
	 */
	public static void main(String[] args) {
		var dataToSerialize = QuickSerializer.outputOf(1024); // Creating a buffer with 1024 bytes
		/*
			Requires 60 bytes.

			2 - type signature (String = 0x10)
			4 - type data size

			...string data

		 */
		dataToSerialize.writeString("Hello world!");

		/*
			Requires only 1 byte because it's a primitive type
		 */
		dataToSerialize.writeBoolean(true);
		dataToSerialize.writeDouble(Math.PI); // 8 bytes

		var serializedData = QuickSerializer.inputOf(dataToSerialize.getBytes()); // 69 bytes

		System.out.println(serializedData.readString());
		System.out.println(serializedData.readBoolean());
		System.out.println(serializedData.readDouble());

		/*
			Only 69 bytes were used here. Accordingly,
			it would have been necessary to allocate exactly that much initially,
			but this is not critical.

			It's much worse if there are not enough bytes, then there will be thrown an exception.
		 */
	}

}