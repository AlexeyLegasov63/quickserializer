package examples;

import org.karma.serialization.*;

public class SecondExample {

	/**
	 <h1>Second example:</h1>

	    Object serializers with QuickSerializer
	 */
	public static void main(String[] args) {
		var dataToSerialize = QuickSerializer.outputOf(1024);

		dataToSerialize.writeObject(new Apple("Red", 0));
		dataToSerialize.writeObject(new Apple("Yellow", 2));
		dataToSerialize.writeObject(new Apple("Green", -3));

		var serializedData = QuickSerializer.inputOf(dataToSerialize.getBytes());

		while (serializedData.hasAvailable()) {
			System.out.println(serializedData.readObject(Apple.class));
		}

		/*
			You need to remember that objects are just numbers
			that represent a signature and the amount of data after them.

			For example:
				Vector2(byte x = 5, byte y = 11) that writing x and y and has 0x300 signature

			Will be:

			0x300 -- Signature (2 bytes)
			0x00000002 -- Integer (4 bytes) Size of data after that
			0x5 -- x
			0xB -- y

			So size of serialized Vector2 equals to 8 bytes.
		 */
	}

	static {
		QuickSerializer.registerSerializer(AppleSerializer.class); // Registering our class-serializer
	}

	/**
		<h1>Apple</h1>

	    Our class to serialization
		Just an example with one String and an Integer
	 */
	public record Apple(String color, int amount) {
		@Override
		public String toString() {
			return "Apple{" +
					"color='" + color + '\'' +
					", amount=" + amount +
					'}';
		}
	}
	/**

		<h1>Our class-serializer:</h1>

		The DataStreams finds the required serializer for a specific object (Apple here) based on the signature.
		A certain number of bytes are allocated for serialization of each object.

		When deserializing, we get a buffer with occupied bytes during serialization.

		For example, we write 4 bytes for an integer in a buffer of size 512 (64 here),
		And when deserializing, we get a buffer of size 4.

		Apple 0xAA
			Color String
			Amount Int32

		Note:
			One object, even an empty one, requires at least 6 bytes to store the signature and buffer size.
			But this does not refer to the object buffer, but to where you write this object.

	 */
	@SerializerObject(signature = 0xAA, bytes = 64)
	public static class AppleSerializer implements Serializer<Apple> {

		@Override
		public void serialize(SerializationOutput data, Apple object) {
			data.writeString(object.color);
			data.writeInt(object.amount);
		}

		@Override
		public Apple deserialize(SerializationInput data) {
			var color = data.readString();
			var amount = data.readInt();
			return new Apple(color, amount);
		}
	}
}