package fun.bb1.config.serializer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.objects.Primitive;

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
 * A simple way to encapsulate serializing/deserializing objects to a targeted format
 * 
 * @param <T> The type of {@link Object} that is to be serialized/deserialized
 * 
 * @author BradBot_1
 */
public interface ISerializer<T> {
	/**
	 * Serializes the provided argument into a form that can be translated
	 * 
	 * @param object The object to serialize
	 * 
	 * @return The serialized form of the object
	 */
	public @NotNull Primitive serialize(@NotNull final T object);
	/**
	 * Deserializes the serialized data from a {@link Primitive}
	 * 
	 * @param serializedData The data in a serialized form
	 * 
	 * @return The object that was represented by the serialized data
	 */
	public @Nullable T deserialize(@NotNull final Primitive serializedData);
	/**
	 * @return The class that represents how the data will be encapsulated after serialization
	 */
	public @NotNull Class<? extends Primitive> getSerializeType();
	/**
	 * @return The class of the object that is to have its data encapsulated
	 */
	public @NotNull Class<? extends T> getObjectType();
	
}
