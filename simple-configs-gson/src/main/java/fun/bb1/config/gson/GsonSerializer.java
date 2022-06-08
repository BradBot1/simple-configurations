package fun.bb1.config.gson;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;

import fun.bb1.config.serializer.ISerializer;

public final class GsonSerializer<R> implements ISerializer<JsonElement, R>{

	private final @NotNull Class<R> clazz;
	private final @NotNull Function<R, JsonElement> serializer;
	private final @NotNull Function<JsonElement, R> deserializer;
	
	GsonSerializer(final @NotNull Class<R> clazz, final @NotNull Function<R, JsonElement> serializer, final @NotNull Function<JsonElement, R> deserializer) {
		this.clazz = clazz;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}
	
	@Override
	public @NotNull Class<JsonElement> getSerializeType() {
		return JsonElement.class;
	}
	
	@Override
	public @NotNull Class<R> getObjectType() {
		return this.clazz;
	}

	@Override
	public @NotNull JsonElement serialize(@NotNull R configToBeSerialized) {
		return this.serializer.apply(configToBeSerialized);
	}

	@Override
	public @Nullable R deserialize(@NotNull JsonElement serializedData) {
		return this.deserializer.apply(serializedData);
	}

}
