package fun.bb1.config.serializer;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.MethodUtils.invokeMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * An {@link IRegistry} implementation thats used for containing serializers
 *
 * @param <T> The serialization type
 * 
 * @author BradBot_1
 */
public class SerializerRegistry<T> implements IRegistry<Class<?>, ISerializer<T, ?>> {
	
	protected final @NotNull Map<Class<?>, ISerializer<T, ?>> map = new ConcurrentHashMap<Class<?>, ISerializer<T, ?>>();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable ISerializer<T, ?> register(@NotNull final Class<?> identifier, @NotNull final ISerializer<T, ?> registree) {
		this.map.put(identifier, registree);
		this.onRegister(identifier, registree);
		return registree;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable ISerializer<T, ?> unregister(@NotNull final Class<?> identifier) {
		return this.map.remove(identifier);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public @Nullable ISerializer<T, ?> get(@NotNull final Class<?> identifier) {
		ISerializer<T, ?> serializer = this.map.get(identifier);
		if (serializer != null) return serializer;
		if (identifier.isArray()) return this.getArraySerializerFor(this.get(identifier.getComponentType()));
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(@NotNull Class<?> identifier) {
		return this.map.containsKey(identifier);
	}
	/**
	 * Creates an {@link ISerializer} that works for an array of the type provided
	 * 
	 * @apiNote Will return null if not supported
	 * 
	 * @param elementSerializer The serializer to make an array serializer for
	 * @return A valid {@link ISerializer} for the provided elementSerializer array
	 */
	protected @Nullable ISerializer<T, ?> getArraySerializerFor(@Nullable final ISerializer<T, ?> elementSerializer) { return null; }
	
	@SuppressWarnings("unchecked")
	@Internal
	public @NotNull T convertMap(@NotNull final Map<String, T> map) {
		return (T) invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), this.get(Map.class), map);
	}
	
	@SuppressWarnings("unchecked")
	@Internal
	public @NotNull Map<String, T> convertMap(@NotNull final T map) {
		return ((Map<String, T>)invokeMethod(handle(()->ISerializer.class.getMethod("deserialize", Object.class)), this.get(Map.class), map))
				.entrySet()
				.stream()
				.collect(() -> new HashMap<String, T>(),
						(t, u) -> t.put(u.getKey(), u.getValue()),
						(t, u) -> t.putAll(u));
	}
}
