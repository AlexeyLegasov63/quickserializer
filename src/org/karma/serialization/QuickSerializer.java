package org.karma.serialization;

import org.karma.serialization.serializers.StringSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

@SuppressWarnings(value = "unchecked")
public class QuickSerializer
{
	// Registered serializers by Class
	private static final HashMap<Class<?>, RuntimeSerializer<?>> SERIALIZERS = new HashMap<>();

	/**
	 * Get a serializer by class.
	 *
	 * @param objectClass A class that required a serializer
	 * @return Class serializer
	 * @param <T> Required type
	 */
	static <T> RuntimeSerializer<T> getSerializer(Class<T> objectClass) {
		return (RuntimeSerializer<T>) SERIALIZERS.get(objectClass);
	}

	/**
	 * Get a serializer by signature.
	 *
	 * @param signature Serializer signature
	 * @return Class serializer
	 * @param <T> Required type
	 */
	static <T> RuntimeSerializer<T> getSerializer(short signature) {
		return (RuntimeSerializer<T>) SERIALIZERS.values()
				.stream()
				.filter(f -> f.signature == signature)
				.findFirst()
				.orElseThrow();
	}

	/**
	 * Register a serializer for an empty object (java.lang.Object for example)
	 *
	 * @param signature Serializer signature
	 * @param objectClass Serializer class
	 * @param <T> Required type
	 */
	public static <T> void resisterEmpty(short signature, Class<T> objectClass) throws NoSuchMethodException {
		var objectConstructor = objectClass.getDeclaredConstructor();
		var objectSerializer = new Serializer<T>() {
			@Override
			public void serialize(SerializationOutput data, T object) {
			}
			@Override
			public T deserialize(SerializationInput data) {
				try {
					return objectConstructor.newInstance();
				} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
					throw new RuntimeException("Failed to create a new instance", e);
				}
			}
		};
		SERIALIZERS.put(objectClass, new RuntimeSerializer<T>(objectClass, objectSerializer, signature, 0));
	}

	private static <T> void registerSerializer(short signature, int bytes, Serializer<T> serializer) {
		var serializerInterfaces = serializer.getClass().getGenericInterfaces();
		assert serializerInterfaces.length != 0;
		var serializerParamType = (ParameterizedType) serializerInterfaces[0];
		var objectClass = (Class<T>) serializerParamType.getActualTypeArguments()[0];
		SERIALIZERS.put(objectClass, new RuntimeSerializer<T>(objectClass, serializer, signature, bytes));
	}

	private static <T> void registerSerializer(Serializer<T> serializer) {
		var serializerClass = serializer.getClass();
		assert serializerClass.isAnnotationPresent(SerializerObject.class);

		var annotationData = serializer.getClass().getDeclaredAnnotation(SerializerObject.class);

		registerSerializer(annotationData.signature(), annotationData.bytes(), serializer);
	}

	/**
	 * Register a serializer for an object.
	 *
	 * @param serializer Serializer class
	 * @param <T> Required type
	 */
	public static <T> void registerSerializer(Class<? extends Serializer<T>> serializer) {
		Serializer<T> serializerInstance = null;
		try {
			serializerInstance = serializer.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		registerSerializer(serializerInstance);
	}

	public static SerializationInput inputOf(byte[] data) {
		return new SerializationInput(data);
	}

	public static ConcurrentSerializationInput concurrentInputOf(byte[] data) {
		return new ConcurrentSerializationInput(data);
	}

	public static SerializationOutput outputOf(int bufferCapacity) {
		assertCapacity(bufferCapacity);
		return new SerializationOutput(bufferCapacity);
	}

	public static ConcurrentSerializationOutput concurrentOutputOf(int bufferCapacity) {
		assertCapacity(bufferCapacity);
		return new ConcurrentSerializationOutput(bufferCapacity);
	}

	private static void assertCapacity(int capacity) {
		if (Integer.MAX_VALUE - capacity >= 0) {
			return;
		}
		throw new OutOfMemoryError();
	}

	static {
		// Register java.lang.String serializer
		registerSerializer(StringSerializer.class);
	}

	record RuntimeSerializer<T>(Class<T> objectClass, Serializer<T> serializerInstance, short signature, int bytes) {
	}
}
