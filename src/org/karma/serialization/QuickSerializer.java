package org.karma.serialization;

import org.karma.serialization.java.StringSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

public class QuickSerializer
{
	private static final HashMap<Class<?>, RuntimeSerializer<?>> SERIALIZERS = new HashMap<>();

	static <T> RuntimeSerializer<T> getSerializer(Class<T> objectClass) {
		return (RuntimeSerializer<T>) SERIALIZERS.get(objectClass);
	}

	static <T> RuntimeSerializer<T> getSerializer(short signature) {
		return (RuntimeSerializer<T>) SERIALIZERS.values()
				.stream()
				.filter(f -> f.signature == signature)
				.findFirst()
				.orElseThrow();
	}

	public static <T> void registerObject(short signature, int bytes, Serializer<T> serializer) {
		var serializerInterfaces = serializer.getClass().getGenericInterfaces();
		assert serializerInterfaces.length != 0;
		var serializerParamType = (ParameterizedType) serializerInterfaces[0];
		var objectClass = (Class<T>) serializerParamType.getActualTypeArguments()[0];
		SERIALIZERS.put(objectClass, new RuntimeSerializer<T>(objectClass, serializer, signature, bytes));
	}

	public static <T> void registerObject(Serializer<T> serializer) {
		var serializerClass = serializer.getClass();
		assert serializerClass.isAnnotationPresent(SerializerObject.class);

		var annotationData = serializer.getClass().getDeclaredAnnotation(SerializerObject.class);

		registerObject(annotationData.signature(), annotationData.bytes(), serializer);
	}

	public static <T> void registerObject(Class<? extends Serializer<T>> serializer) {
		Serializer<T> serializerInstance = null;
		try {
			serializerInstance = serializer.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		registerObject(serializerInstance);
	}

	static {
		registerObject(StringSerializer.class);
	}

	static record RuntimeSerializer<T>(Class<T> objectClass, Serializer<T> serializerInstance, short signature, int bytes) {
	}
}
