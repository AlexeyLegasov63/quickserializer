package org.karma.serialization.serializers;

import org.karma.serialization.SerDataInputStream;
import org.karma.serialization.SerDataOutputStream;
import org.karma.serialization.Serializer;
import org.karma.serialization.SerializerObject;

@SerializerObject(signature = 0x10) // String has a 0x10 signature
public class StringSerializer implements Serializer<String> {

	@Override
	public void serialize(SerDataOutputStream data, String string) {
		var stringLength = string.length();
		data.writeInt(stringLength); // String length
		for (int i = 0; i < stringLength; i++) {
			data.writeInt(string.charAt(i)); // Some char
		}
	}

	@Override
	public String deserialize(SerDataInputStream data) {
		var stringLength = data.readInt(); // String length
		var stringChars = new char[stringLength];
		for (int i = 0; i < stringLength; i++) {
			stringChars[i] = data.readChar(); // Some char
		}
		return new String(stringChars); // Return result
	}

}
