package fun.bb1.config.serializer;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;

import java.lang.reflect.Array;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.objects.Primitive;
import fun.bb1.objects.PrimitiveMap;
import fun.bb1.registry.IRegistry;
import fun.bb1.registry.SimpleRegistry;

public final class SerializerController {
	
	private static final @NotNull IRegistry<Class<?>, ISerializer<?>> SERIALIZER_REGISTRY = new SimpleRegistry<Class<?>, ISerializer<?>>() {{
		this.register(String.class, new InlineSerializer<String>((p)->p.getAsString(), (t)->new Primitive(t)));
		this.register(Character.class, new InlineSerializer<Character>((p)->p.getAsCharacter(), (t)->new Primitive(t)));
		this.register(char.class, new InlineSerializer<Character>((p)->p.getAsCharacter(), (t)->new Primitive(t)));
		this.register(Number.class, new InlineSerializer<Number>((p)->p.getAsNumber(), (t)->new Primitive(t)));
		this.register(Integer.class, new InlineSerializer<Integer>((p)->p.getAsNumber().intValue(), (t)->new Primitive(t)));
		this.register(int.class, new InlineSerializer<Integer>((p)->p.getAsNumber().intValue(), (t)->new Primitive(t)));
		this.register(Double.class, new InlineSerializer<Double>((p)->p.getAsNumber().doubleValue(), (t)->new Primitive(t)));
		this.register(double.class, new InlineSerializer<Double>((p)->p.getAsNumber().doubleValue(), (t)->new Primitive(t)));
		this.register(Float.class, new InlineSerializer<Float>((p)->p.getAsNumber().floatValue(), (t)->new Primitive(t)));
		this.register(float.class, new InlineSerializer<Float>((p)->p.getAsNumber().floatValue(), (t)->new Primitive(t)));
		this.register(Short.class, new InlineSerializer<Short>((p)->p.getAsNumber().shortValue(), (t)->new Primitive(t)));
		this.register(short.class, new InlineSerializer<Short>((p)->p.getAsNumber().shortValue(), (t)->new Primitive(t)));
		this.register(Byte.class, new InlineSerializer<Byte>((p)->p.getAsNumber().byteValue(), (t)->new Primitive(t)));
		this.register(byte.class, new InlineSerializer<Byte>((p)->p.getAsNumber().byteValue(), (t)->new Primitive(t)));
		this.register(Long.class, new InlineSerializer<Long>((p)->p.getAsNumber().longValue(), (t)->new Primitive(t)));
		this.register(long.class, new InlineSerializer<Long>((p)->p.getAsNumber().longValue(), (t)->new Primitive(t)));
		this.register(Boolean.class, new InlineSerializer<Boolean>((p)->p.getAsBoolean(), (t)->new Primitive(t)));
		this.register(boolean.class, new InlineSerializer<Boolean>((p)->p.getAsBoolean(), (t)->new Primitive(t)));
		this.register(Primitive.class, new InlineSerializer<Primitive>((p)->p, (t)->t));
		this.register(PrimitiveMap.class, new InlineSerializer<PrimitiveMap<?>>((p)->p.getAsMap(), (t)->new Primitive(t)));
		this.register(Primitive[].class, new InlineSerializer<Primitive[]>((p)->p.getAsArray(), (t)->new Primitive(t)));
		this.register(Primitive[][].class, new InlineSerializer<Primitive[][]>((p)->{
			final Primitive[] arr = p.getAsArray();
			int maxLength = 0;
			for (final Primitive prim : arr) {
				final int length = prim.getAsArray().length;
				if (maxLength < length) maxLength = length;
			}
			final Primitive[][] retArr = new Primitive[arr.length][maxLength];
			for (int i = 0; i < retArr.length; i++) {
				retArr[i] = arr[i].getAsArray();
			}
			return retArr;
		}, (t)->{
			final Primitive[] arr = new Primitive[t.length];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = new Primitive(t[i]);
			}
			return new Primitive(arr);
		}));
	}};
	
	public static final @NotNull IRegistry<Class<?>, ISerializer<?>> getSerializerRegistry() {
		return SERIALIZER_REGISTRY;
	}
	
	public static final <T> @Nullable Primitive serialize(@NotNull final T type) {
		@SuppressWarnings("unchecked")
		final Class<T> clazz = (Class<T>) type.getClass();
		final ISerializer<T> serializer = getSerializerFor(clazz);
		return handle(()->serializer.serialize(type));
	}
	
	public static final <T> @Nullable T deserialize(@NotNull final Primitive primitive, @NotNull final Class<T> clazz) {
		return handle(()->getSerializerFor(clazz).deserialize(primitive));
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> @Nullable ISerializer<T> getSerializerFor(@NotNull final Class<T> type) {
		final ISerializer<T> translator = (ISerializer<T>) getSerializerRegistry().get(type);
		if (translator != null) return translator;
		if (type.isArray()) {
			int count = 0;
			Class<?> rep = type;
			while (rep.isArray()) {
				count++;
				rep = rep.componentType();
			}
			ISerializer<Object> translator2 = getSerializerRegistry().get(rep).toObjectSerializer();
			if (translator2 == null) return null;
			for (int i = count; i > 0; i--) {
	            rep = rep.arrayType();
	            translator2 = buildArrayTranslator(rep, translator2);
	        }
			return new CastingSerializer<T>(translator2);
		}
		return null;
	}
	
	private static final @Nullable ISerializer<Object> buildArrayTranslator(@NotNull final Class<?> type, @NotNull final ISerializer<?> translator) {
		final ISerializer<Object> serializer = translator.toObjectSerializer();
		return new ISerializer<Object>() {

			@Override
			public @NotNull final Object deserialize(@NotNull final Primitive primitiveForm) {
				final Primitive[] primArr = primitiveForm.getAsArray();
				final Object arr = Array.newInstance(type, primArr.length);
				for (int i = 0; i < primArr.length; i++) {
					if (primArr[i] == null) continue;
					final Object val = serializer.deserialize(primArr[i]);
					Array.set(arr, i, handle(()->type.cast(val))); // cast for primitives
				}
				return arr;
			}

			@Override
			public @NotNull final Primitive serialize(@NotNull Object primitiveForm) {
				if (primitiveForm instanceof Primitive p) primitiveForm = p.getAsArray();
				final Primitive[] primArr = new Primitive[Array.getLength(primitiveForm)];
				for (int i = 0; i < primArr.length; i++) {
					try {
						primArr[i] = serializer.serialize(Array.get(primitiveForm, i));
					} catch (Throwable e) { }
				}
				return new Primitive(primArr);
			}
			
		};
	}
		
}
