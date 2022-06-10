package fun.bb1.config.yaml;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.MethodUtils.invokeMethod;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.serializer.ISerializer;
import fun.bb1.config.serializer.SerializerRegistry;
import fun.bb1.yaml.IYamlElement;
import fun.bb1.yaml.YamlArray;
import fun.bb1.yaml.YamlObject;
import fun.bb1.yaml.YamlPrimitive;

public class YamlSerializerRegistry extends SerializerRegistry<IYamlElement> {
	
	@Override
	protected @Nullable ISerializer<IYamlElement, ?> getArraySerializerFor(@Nullable final ISerializer<IYamlElement, ?> elementSerializer) {
		return new ISerializer<IYamlElement, Object>() {

			@Override
			public @NotNull IYamlElement serialize(@NotNull Object array) {
				final YamlArray yamlArray = new YamlArray();
				final Method method = handle(()->ISerializer.class.getMethod("serialize", Object.class));
				for (int i = 0; i < Array.getLength(array); i++) {
					yamlArray.add((IYamlElement)invokeMethod(method, elementSerializer, Array.get(array, i)));
				}
				return yamlArray;
			}

			@Override
			public @Nullable Object deserialize(@NotNull IYamlElement yaml) {
				final YamlArray yamlArray = yaml.getAsYamlArray();
				final int jsonArraySize = yamlArray.getSize();
				final Object array = Array.newInstance(elementSerializer.getObjectType(), jsonArraySize);
				final Method method = handle(()->ISerializer.class.getMethod("deserialize", Object.class));
				for (int i = 0; i < jsonArraySize; i++) {
					Array.set(array, i, invokeMethod(method, elementSerializer, yamlArray.get(i)));
				}
				return array;
			}

			@Override
			public @NotNull Class<? extends IYamlElement> getSerializeType() {
				return YamlArray.class;
			}

			@Override
			public @NotNull Class<? extends Object> getObjectType() {
				return elementSerializer.getObjectType().arrayType();
			}
			
		};
	}
	
	@Override
	public @NotNull Map<String, IYamlElement> convertMap(@NotNull IYamlElement yaml) {
		final Map<String, IYamlElement> map = new LinkedHashMap<String, IYamlElement>();
		if (yaml instanceof YamlPrimitive prim) return Map.of("unkown", prim);
		if (yaml instanceof YamlArray array) {
			for (int i = 0; i < array.getSize(); i++) {
				map.put(Integer.toString(i), array.get(i));
			}
		} else {
			final YamlObject object = (YamlObject) yaml;
			for (final YamlPrimitive key : object) {
				map.put(key.getAsString(), object.get(key));
			}
		}
		return map;
	}
	
	@Override
	public @NotNull IYamlElement convertMap(@NotNull Map<String, IYamlElement> map) {
		return new YamlObject(map.entrySet().stream().collect(()->new LinkedHashMap<YamlPrimitive, IYamlElement>(),
															  (a, b)->a.put(new YamlPrimitive(b.getKey()), b.getValue()),
															  (a, b)->a.putAll(b)));
	}
	
}
