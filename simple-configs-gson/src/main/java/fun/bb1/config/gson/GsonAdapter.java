package fun.bb1.config.gson;

import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import fun.bb1.config.adapter.AbstractAdapter;
import fun.bb1.objects.Primitive;
import fun.bb1.objects.PrimitiveMap;
import fun.bb1.objects.Tuple;
import fun.bb1.registry.IRegisterable;

public class GsonAdapter extends AbstractAdapter<JsonElement> implements IRegisterable<Void> {
	
	private static final @NotNull String COMMENT_PREFIX = "comment-";
	private final @NotNull Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	GsonAdapter() {
		super(JsonElement.class);
	}

	@Override
	public @Nullable JsonElement translate(@NotNull Primitive primitive) {
		if (primitive.isString() || primitive.isCharacter()) return new JsonPrimitive(primitive.getAsString());
		if (primitive.isNumber()) return new JsonPrimitive(primitive.getAsNumber());
		if (primitive.isBoolean()) return new JsonPrimitive(primitive.getAsBoolean());
		if (primitive.isArray()) {
			final JsonArray jsonArray = new JsonArray();
			for (final Primitive primitive2 : primitive.getAsArray()) {
				jsonArray.add(this.translate(primitive2));
			}
			return jsonArray;
		}
		final JsonObject jsonObject = new JsonObject();
		for (final Entry<String, Primitive> entry : primitive.getAsStringBasedMap().entrySet()) {
			jsonObject.add(entry.getKey(), this.translate(entry.getValue()));
		}
		return jsonObject;
	}
	
	@Override
	public @Nullable Primitive translate(@NotNull JsonElement js) {
		if (js instanceof JsonNull) return null;
		if (js instanceof final JsonArray jsonArray) {
			final Primitive[] primitiveArray = new Primitive[jsonArray.size()];
			for (int i = 0; i < primitiveArray.length; i++) {
				primitiveArray[i] = this.translate(jsonArray.get(i));
			}
			return new Primitive(primitiveArray);
		}
		if (js instanceof final JsonObject jsonObject) {
			final PrimitiveMap<String> primitiveMap = new PrimitiveMap<String>();
			for (final Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				primitiveMap.put(entry.getKey(), this.translate(entry.getValue()));
			}
			return new Primitive(primitiveMap);
		}
		if (js instanceof final JsonPrimitive primitive) {
			if (primitive.isBoolean()) return new Primitive(primitive.getAsBoolean());
			if (primitive.isNumber()) return new Primitive(primitive.getAsNumber());
			if (primitive.isString()) {
				final String str = primitive.getAsString();
				if (str.length() == 1) new Primitive(str.charAt(0));
				return new Primitive(str);
			}
		}
		return new Primitive(js.toString());
	}

	@Override
	public @Nullable JsonElement translateMap(@NotNull Map<String, Tuple<Primitive, String>> map) {
		final JsonObject root = new JsonObject();
		for (final Entry<String, Tuple<Primitive, String>> entry : map.entrySet()) {
			final String key = entry.getKey();
			final @Nullable String comment = entry.getValue().second();
			if (comment != null && !comment.isBlank()) {
				root.add(COMMENT_PREFIX + key, new JsonPrimitive(comment));
			}
			root.add(key, this.translate(entry.getValue().first()));
		}
		return root;
	}

	@Override
	public @NotNull String convertToString(@NotNull JsonElement js) {
		return this.gson.toJson(js);
	}

	@Override
	public @NotNull JsonElement convertFromString(@NotNull String str) {
		return JsonParser.parseString(str);
	}

	@Override
	public @NotNull String getRecommendedFileExtension() {
		return ".conf.json";
	}

}
