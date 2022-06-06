package fun.bb1.config.serializer;

import org.jetbrains.annotations.NotNull;

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
 * @param <S> The serialzation type
 * @param <R> The type of {@link Object} that is to be serialized/deserialized
 * 
 * @author BradBot_1
 */
public interface ISerializer<S, R> {
	/**
	 * Serializes the provided {@link Object}
	 * 
	 * @param configToBeSerialized The {@link Object} that is to be serialized
	 * @return The serialized form of the object
	 */
	public @NotNull S serialize(@NotNull final R configToBeSerialized);
	/**
	 * Deserializes the provied {@link Object}
	 * 
	 * @param serializedData The data in a serialized form
	 */
	public R deserialize(@NotNull final S serializedData);
	/**
	 * @return The class that represents how the data will be encapsulated after serialization
	 */
	public Class<S> getSerializeType();
	/**
	 * @return The class that represents how the data is before serialization
	 */
	public Class<R> getObjectType();
	
}
