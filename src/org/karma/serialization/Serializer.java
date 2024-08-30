package org.karma.serialization;

public interface Serializer<T>
{
	void serialize(SerDataOutputStream data, T object);

	T deserialize(SerDataInputStream data);
}
