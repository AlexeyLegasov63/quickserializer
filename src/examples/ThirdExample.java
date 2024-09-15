package examples;

import org.karma.serialization.*;

import java.util.ArrayList;
import java.util.List;

public class ThirdExample {

	/**
	 <h1>Third example:</h1>

		Serializers for complex objects.

	    Okay, we've already seen how to serialize objects.
	    But what if we have an object with objects inside?

	 */

	public static void main(String[] args) {
		var dataToSerialize = QuickSerializer.outputOf(1024);

		// Our apples
		var apples = List.of(
				new Apple("Red", 1),
				new Apple("Green", 4),
				new Apple("Gold", 0),
				new Apple("Orange", -1)
		);

		var ourBox = new Box(apples); // Filled box
		var emptyBox = new Box(List.of()); // Empty box

		dataToSerialize.writeObject(ourBox);
		dataToSerialize.writeObject(emptyBox);

		var serializedData = QuickSerializer.inputOf(dataToSerialize.getBytes());

		System.out.println(serializedData.readObject(Box.class));

		Box emptyBoxDeserialized = serializedData.readObject(); // We also can read objects by using typecast

		System.out.println(emptyBoxDeserialized);

		/*
			So, we realized that it is possible to serialize objects inside objects,
			they will simply be inside the data buffer of this object,
			forming a tree
		 */
	}

	static {
		// Registering our class-serializers
		QuickSerializer.registerSerializer(AppleSerializer.class);
		QuickSerializer.registerSerializer(BoxSerializer.class);
	}

	/**
	 <h1>Box with Apples</h1>

	    Our complex class to serialization
	 */
	public record Box(List<Apple> apples) {
		@Override
		public String toString() {
			return "Box{" +
					"apples=" + apples +
					'}';
		}
	}

	/**
		 <h1>Our complex class-serializer:</h1>

	     This serializer works with Box and has signature 0xA0

	     To write an object all you need to do is just use writeObject.
	     But there's an important requirement: your object must have a registered serializer.
	     Otherwise you will get an exception.

	 */
	@SerializerObject(signature = 0xA0)
	public static class BoxSerializer implements Serializer<Box> {

		@Override
		public void serialize(SerializationOutput data, Box object) {
			var boxApples = object.apples;
			var boxSize = boxApples.size();

			data.writeInt(boxSize);
			for (int i = 0; i < boxSize; i++) {
				data.writeObject(boxApples.get(i));
			}
		}

		@Override
		public Box deserialize(SerializationInput data) {
			var boxApples = new ArrayList<Apple>(); // New list

			// J means the size of the list
			for (int i = 0, j = data.readInt(); i < j; i++) {
				boxApples.add(data.readObject());
			}

			return new Box(boxApples); // Return deserialized instance
		}
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