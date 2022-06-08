package fun.bb1.config.gson;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.MethodUtils.invokeMethod;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fun.bb1.config.serializer.ISerializer;
import fun.bb1.config.serializer.SerializerRegistry;

public class GsonSerializerRegistry extends SerializerRegistry<JsonElement> {
	
	@Override
	protected @Nullable ISerializer<JsonElement, ?> getArraySerializerFor(@Nullable final ISerializer<JsonElement, ?> elementSerializer) {
		return new ISerializer<JsonElement, Object>() {

			@Override
			public @NotNull JsonElement serialize(@NotNull Object array) {
				final JsonArray jsonArray = new JsonArray();
				final Method method = handle(()->ISerializer.class.getMethod("serialize", Object.class));
				for (int i = 0; i < Array.getLength(array); i++) {
					jsonArray.add((JsonElement)invokeMethod(method, elementSerializer, Array.get(array, i)));
				}
				return jsonArray;
			}

			@Override
			public @Nullable Object deserialize(@NotNull JsonElement json) {
				final JsonArray jsonArray = json.getAsJsonArray();
				final int jsonArraySize = jsonArray.size();
				final Object array = Array.newInstance(elementSerializer.getObjectType(), jsonArraySize);
				final Method method = handle(()->ISerializer.class.getMethod("deserialize", Object.class));
				for (int i = 0; i < jsonArraySize; i++) {
					Array.set(array, i, invokeMethod(method, elementSerializer, jsonArray.get(i)));
				}
				return array;
			}

			@Override
			public @NotNull Class<JsonElement> getSerializeType() {
				return JsonElement.class;
			}

			@Override
			public @NotNull Class<? extends Object> getObjectType() {
				return elementSerializer.getObjectType().arrayType();
			}
			
		};
	}
	
	@Override
	public @NotNull Map<String, JsonElement> convertMap(@NotNull JsonElement js) {
		final Map<String, JsonElement> map = new TreeMap<String, JsonElement>();
		if (!js.isJsonObject()) return map;
		js.getAsJsonObject().entrySet().forEach((e)->map.put(e.getKey(), e.getValue()));
		return map;
	}
	
	@Override
	public @NotNull JsonElement convertMap(@NotNull Map<String, JsonElement> map) {
		final JsonObject js = new JsonObject();
		map.entrySet().forEach((e)->js.add(e.getKey(), e.getValue()));
		return js;
	}
	
}
