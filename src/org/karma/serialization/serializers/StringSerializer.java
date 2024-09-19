package org.karma.serialization.serializers;

import org.karma.serialization.SerializationInput;
import org.karma.serialization.SerializationOutput;
import org.karma.serialization.Serializer;
import org.karma.serialization.SerializerObject;

@SerializerObject(signature = 0x10) // String has a 0x10 signature
public class StringSerializer implements Serializer<String> {

	@Override
	public void serialize(SerializationOutput data, String string) {
		byte[] stringBytes = string.getBytes();
		int stringLength = stringBytes.length;
		data.writeInt(stringLength); // String length
		for (int i = 0; i < stringLength; i++) {
			data.writeByte(stringBytes[i]); // Some char
		}
	}

	@Override
	public String deserialize(SerializationInput data) {
		int stringLength = data.readInt(); // String length
		byte[] stringBytes = new byte[stringLength];
		for (int i = 0; i < stringLength; i++) {
			stringBytes[i] = data.readByte(); // Some char
		}
		return new String(stringBytes); // Return result
	}

}
