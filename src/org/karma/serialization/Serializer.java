package org.karma.serialization;

import java.lang.reflect.InvocationTargetException;

public interface Serializer<T>
{
	void serialize(SerDataOutputStream data, T object);

	T deserialize(SerDataInputStream data) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
