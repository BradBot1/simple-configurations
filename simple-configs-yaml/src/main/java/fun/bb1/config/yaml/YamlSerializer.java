package fun.bb1.config.yaml;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.serializer.ISerializer;
import fun.bb1.yaml.IYamlElement;

public final class YamlSerializer<R> implements ISerializer<IYamlElement, R>{

	private final @NotNull Class<R> clazz;
	private final @NotNull Function<R, IYamlElement> serializer;
	private final @NotNull Function<IYamlElement, R> deserializer;
	
	YamlSerializer(final @NotNull Class<R> clazz, final @NotNull Function<R, IYamlElement> serializer, final @NotNull Function<IYamlElement, R> deserializer) {
		this.clazz = clazz;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}
	
	@Override
	public @NotNull Class<IYamlElement> getSerializeType() {
		return IYamlElement.class;
	}
	
	@Override
	public @NotNull Class<R> getObjectType() {
		return this.clazz;
	}

	@Override
	public @NotNull IYamlElement serialize(@NotNull R configToBeSerialized) {
		return this.serializer.apply(configToBeSerialized);
	}

	@Override
	public @Nullable R deserialize(@NotNull IYamlElement serializedData) {
		return this.deserializer.apply(serializedData);
	}

}
