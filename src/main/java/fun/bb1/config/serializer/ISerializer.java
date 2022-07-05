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
 * A simple way to encapsulate serializing objects to/from a targeted format
 * 
 * @param <T> The type to translate to
 * 
 * @author BradBot_1
 */
public interface ISerializer<T> {
	/**
	 * Deserializes the provided {@link Primitive} into T
	 * 
	 * @apiNote Will return null if the provided primitiveForm cannot be deserialized
	 * 
	 * @param primitiveForm The serialized version of T
	 * 
	 * @return A deserialized object
	 */
	public @Nullable T deserialize(@NotNull final Primitive primitiveForm);
	/**
	 * Serializes the provided T into {@link Primitive}
	 * 
	 * @apiNote Will return null if the provided instanceOfT cannot be serialized
	 * 
	 * @param instanceOfT The instance of T to be serialized
	 * 
	 * @return A serialized object
	 */
	public @Nullable Primitive serialize(@NotNull final T instanceOfT);
	
}
