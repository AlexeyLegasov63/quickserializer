package org.karma.serialization;

import java.lang.reflect.InvocationTargetException;

public interface Serializer<T>
{
	void serialize(SerializationOutput data, T object);

	T deserialize(SerializationInput data) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
