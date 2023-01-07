package fun.bb1.config.v2;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.v2.annotations.Configurable;

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
 * An interface to aid with configuration
 * <br>
 * This class looks for the {@link Configurable} annotation and uses that to determine what should be serialized
 * <br>
 * For a whole class to represent a config look to {@link fun.bb1.config.Configurable}
 * 
 * @author BradBot_1
 */
public interface IAnnotatedConfigurable extends IConfigurable {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public default <T> @Nullable T serializeForConfiguration(Class<T> serializeType, @Nullable final Logger logger) {
		return this.serializeForConfiguration(serializeType, logger, (f)->f.isAnnotationPresent(Configurable.class));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public default <T> void deserializeFromConfiguration(Class<T> serializeType, @NotNull final T configuration, @Nullable final Logger logger) {
		this.deserializeFromConfiguration(serializeType, configuration, logger, (f)->f.isAnnotationPresent(Configurable.class));
	}
}
