package org.karma.serialization;

import org.karma.serialization.serializers.StringSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
				.findFirst().orElseThrow(() -> new SerializerObjectUnknownException("There's no such serializer with this signature: " + signature));
	}

	/**
	 * Register a serializer for an empty object (java.lang.Object for example)
	 *
	 * @param signature Serializer signature
	 * @param objectClass Serializer class
	 * @param <T> Required type
	 */
	public static <T> void resisterEmpty(short signature, Class<T> objectClass) throws NoSuchMethodException {
		Constructor<T> objectConstructor = objectClass.getDeclaredConstructor();
		Serializer<T> objectSerializer = new Serializer<T>() {
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
		Type[] serializerInterfaces = serializer.getClass().getGenericInterfaces();
		assert serializerInterfaces.length != 0;
		ParameterizedType serializerParamType = (ParameterizedType) serializerInterfaces[0];
		Class<T> objectClass = (Class<T>) serializerParamType.getActualTypeArguments()[0];
		SERIALIZERS.put(objectClass, new RuntimeSerializer<T>(objectClass, serializer, signature, bytes));
	}

	private static <T> void registerSerializer(Serializer<T> serializer) {
		Class<?> serializerClass = serializer.getClass();
		assert serializerClass.isAnnotationPresent(SerializerObject.class);

		SerializerObject annotationData = serializer.getClass().getDeclaredAnnotation(SerializerObject.class);

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

	static class RuntimeSerializer<T>{

		private final Class<T> objectClass;
		private final Serializer<T> serializerInstance;
		private final short signature;
		private final int bytes;


		private RuntimeSerializer(Class<T> objectClass, Serializer<T> serializerInstance, short signature, int bytes) {
			this.objectClass = objectClass;
			this.serializerInstance = serializerInstance;
			this.signature = signature;
			this.bytes = bytes;
		}

		public Class<T> objectClass() {
			return objectClass;
		}

		public Serializer<T> serializerInstance() {
			return serializerInstance;
		}

		public short signature() {
			return signature;
		}

		public int bytes() {
			return bytes;
		}
	}
}
