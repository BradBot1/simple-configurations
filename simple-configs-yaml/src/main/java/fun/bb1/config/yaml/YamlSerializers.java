package fun.bb1.config.yaml;

import static fun.bb1.config.serializer.SerializerRegistryRegistry.registerSerializer;
import static fun.bb1.exceptions.handler.ExceptionHandler.handle;

import java.util.function.Function;

import fun.bb1.config.serializer.SerializerRegistryRegistry;
import fun.bb1.yaml.IYamlElement;
import fun.bb1.yaml.YamlArray;
import fun.bb1.yaml.YamlObject;
import fun.bb1.yaml.YamlPrimitive;

public final class YamlSerializers {

	public static final void registerSerializers() {
		final YamlSerializerRegistry registry = new YamlSerializerRegistry();
		SerializerRegistryRegistry.addRegistryFor(IYamlElement.class, registry); // add support for arrays
		/* String and characters */
		registerSerializer(new YamlSerializer<>(CharSequence.class, (s)->new YamlPrimitive(s.toString()), (r)->r instanceof YamlPrimitive prim ? prim.getAsString() : null));
		registerSerializer(new YamlSerializer<>(StringBuffer.class, (s)->new YamlPrimitive(s.toString()), (r)->new StringBuffer(r instanceof YamlPrimitive prim ? prim.getAsString() : "")));
		registerSerializer(new YamlSerializer<>(StringBuilder.class, (s)->new YamlPrimitive(s.toString()), (r)->new StringBuilder(r instanceof YamlPrimitive prim ? prim.getAsString() : "")));
		registerSerializer(new YamlSerializer<>(String.class, (s)->new YamlPrimitive(s), (r)->r instanceof YamlPrimitive prim ? prim.getAsString() : null));
		registerSerializer(new YamlSerializer<>(Character.class, (s)->new YamlPrimitive(s), (r)->r instanceof YamlPrimitive prim ? prim.getAsCharacter() : null));
		registerSerializer(new YamlSerializer<>(char.class, (s)->new YamlPrimitive(s), (r)->r instanceof YamlPrimitive prim ? prim.getAsCharacter() : 'a'));
		/* Numbers */
		final Function<IYamlElement, Number> numberDeserialize = (r) -> r instanceof YamlPrimitive prim ? prim.isNumber() ? prim.getAsNumber() : null : null;
		registerSerializer(new YamlSerializer<>(Number.class, (s)->new YamlPrimitive(s), (r)->numberDeserialize.apply(r)));
		registerSerializer(new YamlSerializer<>(Integer.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).intValue())));
		registerSerializer(new YamlSerializer<>(int.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).intValue(), 0)));
		registerSerializer(new YamlSerializer<>(Double.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).doubleValue())));
		registerSerializer(new YamlSerializer<>(double.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).doubleValue(), 0d)));
		registerSerializer(new YamlSerializer<>(Float.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).floatValue())));
		registerSerializer(new YamlSerializer<>(float.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).floatValue(), 0f)));
		registerSerializer(new YamlSerializer<>(Long.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).longValue())));
		registerSerializer(new YamlSerializer<>(long.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).longValue(), 0l)));
		registerSerializer(new YamlSerializer<>(Short.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).shortValue())));
		registerSerializer(new YamlSerializer<>(short.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).shortValue(), (short) 0)));
		registerSerializer(new YamlSerializer<>(Byte.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).byteValue())));
		registerSerializer(new YamlSerializer<>(byte.class, (s)->new YamlPrimitive(s), (r)->handle(()->numberDeserialize.apply(r).byteValue(), (byte) 0 )));
		/* Boolean */
		registerSerializer(new YamlSerializer<>(Boolean.class, (s)->new YamlPrimitive(s), (r)->r instanceof YamlPrimitive prim ? prim.getAsBoolean() : null));
		registerSerializer(new YamlSerializer<>(boolean.class, (s)->new YamlPrimitive(s), (r)->r instanceof YamlPrimitive prim ? prim.getAsBoolean() : false));
		/* Yaml junk */
		registerSerializer(new YamlSerializer<>(IYamlElement.class, (s)->s, (r)->r));
		registerSerializer(new YamlSerializer<>(YamlArray.class, (s)->s, (r)->r.getAsYamlArray()));
		registerSerializer(new YamlSerializer<>(YamlPrimitive.class, (s)->s, (r)->r.getAsYamlPrimitive()));
		registerSerializer(new YamlSerializer<>(YamlObject.class, (s)->s, (r)->r.getAsYamlObject()));
	}
	
	private YamlSerializers() { }
	
}
