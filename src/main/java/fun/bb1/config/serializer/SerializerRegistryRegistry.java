package fun.bb1.config.serializer;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import fun.bb1.registry.IRegistry;

/**
 * 
 * Copyright 2022 BradBot_1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * A Registry containing {@link IRegistry}s that contain serializers
 * 
 * @author BradBot_1
 */
public class SerializerRegistryRegistry {
	/**
	 * The storage place for all {@link IRegistry}'s that pertain to configuration storage
	 */
	private static final Map<Class<?>, SerializerRegistry<?>> MAP = new HashMap<Class<?>, SerializerRegistry<?>>();
	/**
	 * Forwards to {@link #registerSerializer(ISerializer, boolean)} with force set to false
	 * 
	 * @see #registerSerializer(ISerializer, boolean)
	 */
	public static final <T> boolean registerSerializer(@NotNull final ISerializer<T, ?> serializer) {
		return registerSerializer(serializer, false);
	}
	/**
	 * Registers the provided {@link ISerializer}
	 * 
	 * @param serializer The {@link ISerializer} to register
	 * @param force If to override any pre-existing entries
	 * @return If the registration occurred
	 */
	public static final <T> boolean registerSerializer(@NotNull final ISerializer<T, ?> serializer, boolean force) {
		final SerializerRegistry<T> registry = getRegistryFor(serializer.getSerializeType());
		if (force) registry.unregister(serializer.getObjectType());
		if (registry.contains(serializer.getObjectType())) return false; // already one registered
		registry.register(serializer.getObjectType(), serializer);
		return true;
	}
	/**
	 * Gets and returns the appropriate {@link IRegistry} for the provided {@link Class}, if none exists one is created
	 * 
	 * @param serializeType The {@link Class} of the type you want to serialize too
	 * @return The appropriate {@link IRegistry}
	 */
	@SuppressWarnings("unchecked") // this is safe as the registry must be the same type as the serializeType
	public static final <T> @NotNull SerializerRegistry<T> getRegistryFor(@NotNull final Class<T> serializeType) {
		if (!MAP.containsKey(serializeType)) MAP.put(serializeType, new SerializerRegistry<T>());
		return (@NotNull SerializerRegistry<T>) MAP.get(serializeType);
	}
	/**
	 * Registers a custom SerializerRegistry for a given serializeType
	 * 
	 * @param serializeType The {@link Class} of the type you want to serialize too
	 * @param registry A {@link SerializerRegistry} that will be returned when requested with the serializeType argument
	 * @return If the registration occurred
	 */
	public static final <T> boolean addRegistryFor(@NotNull final Class<T> serializeType, @NotNull final SerializerRegistry<T> registry) {
		if (MAP.containsKey(serializeType)) return false;
		MAP.put(serializeType, registry);
		return true;
	}
	/**
	 * To stop instantiation
	 */
	private SerializerRegistryRegistry() { }
	
}
