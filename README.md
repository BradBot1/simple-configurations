# simple-configurations

### Updating from V1.0.0 -> V2.0.0

> If you are just using the `#serializeForConfiguration` and `#deserializeFromConfiguration` methods you do not need to do anything

##### Serializer changes

`ISerializer`s now convert to `Primitives`, meaning a serializer will work for any conversion (this lowers the amount of things that someone needs to support)

This means to move to add a conversion type you will need to add a `AbstractAdapter`, you can register it in the `AdapterController`

### Serializers

`ISerializer`s is the only way to add support for an object, any object implementing the `ISerializer` interface can be registered via the registry (you can get the registry from `SerializerController#getSerializerRegistry`)

> It's recommended to utilise the `InlineSerializer` class for the quick creation of serializers

```java
/**
 * An example record to serialize
 */
record Example(@NotNull String apple, int banana) { }

SerializerController.getSerializerRegistry().register(Example.class, new InlineSerializer<Example>((p)->{
	final PrimitiveMap<String> map = p.getAsStringBasedMap(); // convert primitive into a map
	return new Example(map.get("apple").getAsString(), map.get("banana").getAsNumber().intValue()); // create new Example object with its values from the map
}, (t)->{
	final PrimitiveMap<String> map = new PrimitiveMap<String>(); // create a new map
	map.put("apple", new Primitive(t.a())); // put in the map with the key apple
	map.put("banana", new Primitive(t.b())); // put in the map with the key banana
	return new Primitive(map); // wrap into a primitive
}));
```

#### Serializing an object

To quickly serialize an object into a primitive you can call the `SerializerController#serialize` method

```java
Primitive example = SerializerController.serialize(String[] { "what", "a", "funky", "!" });
```

#### Deserializing an object

To quickly deserialize a primitive into an object you can call the `SerializerController#deserialize` method

```java
String[] example = SerializerController.deserialize(new Primitive(new Primitive[] { new Primitive("what"), new Primitive("a"), new Primitive("funky"), new Primitive("!")}));
```

### Adapters

`AbstractAdapters` are used to convert a primitive into a given type, they can be registered via the registry (you can get it from `AdapterController#getAdapterRegistry()`)

Example to come (maybe)
