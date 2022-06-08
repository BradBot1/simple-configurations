package fun.bb1.config.gson;

import static fun.bb1.config.serializer.SerializerRegistryRegistry.registerSerializer;
import static fun.bb1.exceptions.handler.ExceptionHandler.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import fun.bb1.config.serializer.SerializerRegistryRegistry;

public final class GsonSerializers {
	/**
	 * Registers a collection of serializers to allow Gson to be used for configuration
	 */
	public static final void registerSerializers() {
		SerializerRegistryRegistry.addRegistryFor(JsonElement.class, new GsonSerializerRegistry()); // add support for arrays
		/* String and characters */
		registerSerializer(new GsonSerializer<>(CharSequence.class, (s)->new JsonPrimitive(s.toString()), (r)->r instanceof JsonPrimitive prim ? prim.getAsString() : null));
		registerSerializer(new GsonSerializer<>(StringBuffer.class, (s)->new JsonPrimitive(s.toString()), (r)->new StringBuffer(r instanceof JsonPrimitive prim ? prim.getAsString() : "")));
		registerSerializer(new GsonSerializer<>(StringBuilder.class, (s)->new JsonPrimitive(s.toString()), (r)->new StringBuilder(r instanceof JsonPrimitive prim ? prim.getAsString() : "")));
		registerSerializer(new GsonSerializer<>(String.class, (s)->new JsonPrimitive(s), (r)->r instanceof JsonPrimitive prim ? prim.getAsString() : null));
		registerSerializer(new GsonSerializer<>(Character.class, (s)->new JsonPrimitive(s), (r)->r instanceof JsonPrimitive prim ? prim.getAsCharacter() : null));
		registerSerializer(new GsonSerializer<>(char.class, (s)->new JsonPrimitive(s), (r)->r instanceof JsonPrimitive prim ? prim.getAsCharacter() : 'a'));
		/* Numbers */
		final Function<JsonElement, Number> numberDeserialize = (r) -> r instanceof JsonPrimitive prim ? prim.isNumber() ? prim.getAsNumber() : null : null;
		registerSerializer(new GsonSerializer<>(Number.class, (s)->new JsonPrimitive(s), (r)->numberDeserialize.apply(r)));
		registerSerializer(new GsonSerializer<>(Integer.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).intValue())));
		registerSerializer(new GsonSerializer<>(int.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).intValue(), 0)));
		registerSerializer(new GsonSerializer<>(Double.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).doubleValue())));
		registerSerializer(new GsonSerializer<>(double.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).doubleValue(), 0d)));
		registerSerializer(new GsonSerializer<>(Float.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).floatValue())));
		registerSerializer(new GsonSerializer<>(float.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).floatValue(), 0f)));
		registerSerializer(new GsonSerializer<>(Long.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).longValue())));
		registerSerializer(new GsonSerializer<>(long.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).longValue(), 0l)));
		registerSerializer(new GsonSerializer<>(Short.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).shortValue())));
		registerSerializer(new GsonSerializer<>(short.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).shortValue(), (short) 0)));
		registerSerializer(new GsonSerializer<>(Byte.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).byteValue())));
		registerSerializer(new GsonSerializer<>(byte.class, (s)->new JsonPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).byteValue(), (byte) 0 )));
		/* Boolean */
		registerSerializer(new GsonSerializer<>(Boolean.class, (s)->new JsonPrimitive(s), (r)->r instanceof JsonPrimitive prim ? prim.getAsBoolean() : null));
		registerSerializer(new GsonSerializer<>(boolean.class, (s)->new JsonPrimitive(s), (r)->r instanceof JsonPrimitive prim ? prim.getAsBoolean() : false));
		/* Gson junk */
		final Gson gson = new Gson();
		registerSerializer(new GsonSerializer<>(JsonElement.class, (s)->s, (r)->r));
		registerSerializer(new GsonSerializer<>(Map.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, HashMap.class)));
		registerSerializer(new GsonSerializer<>(HashMap.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, HashMap.class)));
		registerSerializer(new GsonSerializer<>(SortedMap.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, TreeMap.class)));
		registerSerializer(new GsonSerializer<>(TreeMap.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, TreeMap.class)));
		registerSerializer(new GsonSerializer<>(ConcurrentHashMap.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, ConcurrentHashMap.class)));
		registerSerializer(new GsonSerializer<>(List.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, ArrayList.class)));
		registerSerializer(new GsonSerializer<>(ArrayList.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, ArrayList.class)));
		registerSerializer(new GsonSerializer<>(LinkedList.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, LinkedList.class)));
		registerSerializer(new GsonSerializer<>(Set.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, HashSet.class)));
		registerSerializer(new GsonSerializer<>(HashSet.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, HashSet.class)));
		registerSerializer(new GsonSerializer<>(SortedSet.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, TreeSet.class)));
		registerSerializer(new GsonSerializer<>(TreeSet.class, (s)->gson.toJsonTree(s), (r)->gson.fromJson(r, TreeSet.class)));
	}
	
	private GsonSerializers() { }
	
}
