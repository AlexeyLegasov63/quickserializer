package org.karma.serialization.java;

import org.karma.serialization.SerDataInputStream;
import org.karma.serialization.SerDataOutputStream;
import org.karma.serialization.Serializer;
import org.karma.serialization.SerializerObject;

@SerializerObject(signature = 0x10)
public class StringSerializer implements Serializer<String> {

	@Override
	public void serialize(SerDataOutputStream data, String string) {
		var stringLength = string.length();
		data.writeInt(stringLength);
		for (int i = 0; i < stringLength; i++) {
			data.writeInt(string.charAt(i));
		}
	}

	@Override
	public String deserialize(SerDataInputStream data) {
		var stringLength = data.readInt();
		var stringChars = new char[stringLength];
		for (int i = 0; i < stringLength; i++) {
			stringChars[i] = data.readChar();
		}
		return new String(stringChars);
	}

}
