package fun.bb1.config.yaml;

import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.adapter.AbstractAdapter;
import fun.bb1.objects.Primitive;
import fun.bb1.objects.PrimitiveMap;
import fun.bb1.objects.Tuple;
import fun.bb1.yaml.IYamlElement;
import fun.bb1.yaml.Yaml;
import fun.bb1.yaml.YamlArray;
import fun.bb1.yaml.YamlObject;
import fun.bb1.yaml.YamlPrimitive;

public class YamlAdapter extends AbstractAdapter<IYamlElement> {

	private final @NotNull Yaml yaml = new Yaml();

	YamlAdapter() {
		super(IYamlElement.class);
	}

	@Override
	public @Nullable IYamlElement translate(@NotNull Primitive primitive) {
		if (primitive.isString()) return new YamlPrimitive(primitive.getAsString());
		if (primitive.isCharacter()) return new YamlPrimitive(primitive.getAsCharacter());
		if (primitive.isNumber()) return new YamlPrimitive(primitive.getAsNumber());
		if (primitive.isBoolean()) return new YamlPrimitive(primitive.getAsBoolean());
		if (primitive.isArray()) {
			final YamlArray ymlonArray = new YamlArray();
			for (final Primitive primitive2 : primitive.getAsArray()) {
				ymlonArray.add(this.translate(primitive2));
			}
			return ymlonArray;
		}
		final YamlObject ymlonObject = new YamlObject();
		for (final Entry<String, Primitive> entry : primitive.getAsStringBasedMap().entrySet()) {
			ymlonObject.add(entry.getKey(), this.translate(entry.getValue()));
		}
		return ymlonObject;
	}

	@Override
	public @Nullable Primitive translate(@NotNull IYamlElement yml) {
		if (yml instanceof final YamlArray ymlArray) {
			final Primitive[] primitiveArray = new Primitive[ymlArray.getSize()];
			for (int i = 0; i < primitiveArray.length; i++) {
				primitiveArray[i] = this.translate(ymlArray.get(i));
			}
			return new Primitive(primitiveArray);
		}
		if (yml instanceof final YamlObject ymlObject) {
			final PrimitiveMap<String> primitiveMap = new PrimitiveMap<String>();
			for (final YamlPrimitive key : ymlObject) {
				primitiveMap.put(key.getAsString(), this.translate(ymlObject.get(key)));
			}
			return new Primitive(primitiveMap);
		}
		if (yml instanceof final YamlPrimitive primitive) {
			if (primitive.isBoolean()) return new Primitive(primitive.getAsBoolean());
			if (primitive.isNumber()) return new Primitive(primitive.getAsNumber());
			if (primitive.isString()) {
				final String str = primitive.getAsString();
				if (str.length() == 1) new Primitive(str.charAt(0));
				return new Primitive(str);
			}
		}
		return new Primitive(yml.toString());
	}

	@Override
	public @Nullable IYamlElement translateMap(@NotNull Map<String, Tuple<Primitive, String>> map) {
		final YamlObject root = new YamlObject();
		for (final Entry<String, Tuple<Primitive, String>> entry : map.entrySet()) {
			final IYamlElement elem = this.translate(entry.getValue().first());
			final @Nullable String comment = entry.getValue().second();
			if (comment != null && !comment.isBlank()) {
				elem.setComment(comment);
			}
			root.add(entry.getKey(), elem);
		}
		return root;
	}

	@Override
	public @NotNull String convertToString(@NotNull IYamlElement primitive) {
		return this.yaml.toYaml(primitive);
	}

	@Override
	public @NotNull YamlObject convertFromString(@NotNull String primitive) {
		return this.yaml.parseString(primitive, YamlObject.class);
	}

	@Override
	public @NotNull String getRecommendedFileExtension() {
		return ".conf.yml";
	}

}
