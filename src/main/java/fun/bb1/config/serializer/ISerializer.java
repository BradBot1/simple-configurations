package fun.bb1.config.serializer;

import org.jetbrains.annotations.NotNull;

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
 * A simple way to encapsulate translating objects to/from a targeted format
 * 
 * @param <R> The type to translate to
 * 
 * @author BradBot_1
 */
public interface ISerializer<T> {
	
	public @NotNull T translate(@NotNull final Primitive primitiveForm);
	
	public @NotNull Primitive translate(@NotNull final T primitiveForm);
	
}
