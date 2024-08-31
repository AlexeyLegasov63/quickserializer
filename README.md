# QuickSerializer

Fast and easy to use data serializer.
All you need is to create an instance of the class - done. Now you serialize the data.

To serialize your classes, you will need to write a serializer for each of them.
```java

@SerializerObject(signature = 0xA70)
public class Vector2dSerializer implements Serializer<Vector2d> {

	@Override
	public void serialize(SerDataOutputStream data, Vector2d object) {
		data.writeDouble(object.x);
		data.writeDouble(object.y);
	}

	@Override
	public Vector2d deserialize(SerDataInputStream data) {
		var x = data.readDouble();
		var y = data.readDouble();
		return new Vector2d(x, y);
	}

}

```
If one object fails to load due to an error, it will not affect the following ones, since a sub-buffer is created for loading each object.
