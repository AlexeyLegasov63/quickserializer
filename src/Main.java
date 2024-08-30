import org.karma.serialization.*;
import org.karma.serialization.QuickSerializer.*;

import java.util.Arrays;

public class Main {
	public static void main(String[] args) {

		QuickSerializer.registerObject(AppleSerializer.class); // Registering our class

		var serData = new SerDataOutputStream(1024);

		serData.writeObject(new Apple("Red", 5));
		serData.writeObject(new Apple("Green", 13));
		serData.writeObject(new Apple("Yellow", 1));

		var serRes = new SerDataInputStream(serData.getFilledBuffer());

		System.out.println(serRes.readObject(Apple.class));
		System.out.println(serRes.readObject(Apple.class));

		System.out.println((Apple)serRes.readObject()); // We can also just use type cast
	}

	public static class Apple {
		public final String color;
		public final int amount;

		public Apple(String color, int amount) {
			this.color = color;
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "Apple{" +
					"color='" + color + '\'' +
					", amount=" + amount +
					'}';
		}
	}

	@SerializerObject(signature = 0xAA, bytes = 64)
	public static class AppleSerializer implements Serializer<Apple> {

		@Override
		public void serialize(SerDataOutputStream data, Apple object) {
			data.writeString(object.color);
			data.writeInt(object.amount);
		}

		@Override
		public Apple deserialize(SerDataInputStream data) {
			var color = data.readString();
			var amount = data.readInt();
			return new Apple(color, amount);
		}
	}
}