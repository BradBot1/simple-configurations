package fun.bb1.config.serializer;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import fun.bb1.registry.IRegistry;
import fun.bb1.registry.SimpleRegistry;

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
public class SerializerRegistry {
	/**
	 * The storage place for all {@link IRegistry}'s that pertain to configuration storage
	 */
	private static final Map<Class<?>, IRegistry<Class<?>, ISerializer<?, ?>>> MAP = new HashMap<Class<?>, IRegistry<Class<?>, ISerializer<?, ?>>>();
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
		final IRegistry<Class<?>, ISerializer<?, ?>> registry = getRegistryFor(serializer.getSerializeType());
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
	public static final <T> @NotNull IRegistry<Class<?>, ISerializer<?, ?>> getRegistryFor(@NotNull final Class<T> serializeType) {
		if (!MAP.containsKey(serializeType)) return MAP.put(serializeType, new SimpleRegistry<Class<?>, ISerializer<?, ?>>());
		return MAP.get(serializeType);
	}
	/**
	 * To stop instantiation
	 */
	private SerializerRegistry() { }
	
}
