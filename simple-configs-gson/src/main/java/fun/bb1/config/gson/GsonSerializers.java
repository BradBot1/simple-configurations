package fun.bb1.config.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fun.bb1.config.serializer.InlineSerializer;

public final class GsonSerializers {
	/**
	 * Registers a collection of serializers to allow Gson to be used for configuration
	 * 
	 * @apiNote This also registers the {@link GsonAdapter}
	 */
	public static final void registerSerializers() {
		final GsonAdapter adapter = new GsonAdapter();
		adapter.register();
		new InlineSerializer<JsonElement>((p)->adapter.translate(p), (j)->adapter.translate(j)).register(JsonElement.class);
		new InlineSerializer<JsonPrimitive>((p)->adapter.translate(p).getAsJsonPrimitive(), (j)->adapter.translate(j)).register(JsonPrimitive.class);
		new InlineSerializer<JsonArray>((p)->adapter.translate(p).getAsJsonArray(), (j)->adapter.translate(j)).register(JsonArray.class);
		new InlineSerializer<JsonObject>((p)->adapter.translate(p).getAsJsonObject(), (j)->adapter.translate(j)).register(JsonObject.class);
		new InlineSerializer<JsonElement>((p)->{ throw new UnsupportedOperationException("Cannot serialize to null"); }, (j)->{ throw new UnsupportedOperationException("Cannot deserialize from null"); }).register(JsonNull.class);
	}
	
	private GsonSerializers() { }
	
}
