package fun.bb1.config;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.FieldUtils.getField;
import static fun.bb1.reflection.FieldUtils.getInheritedFields;
import static fun.bb1.reflection.FieldUtils.setField;
import static fun.bb1.reflection.MethodUtils.invokeMethod;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isTransient;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.serializer.ISerializer;
import fun.bb1.config.serializer.SerializerRegistry;
import fun.bb1.config.serializer.SerializerRegistryRegistry;

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
 * A configuration that doesn't rely on annotations
 * 
 * @apiNote The annotation {@link fun.bb1.config.annotations.Configurable} can still be used to control the serialization name and commenting
 * 
 * @author BradBot_1
 */
public class Configurable implements IAnnotatedConfigurable {
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> @Nullable T serializeForConfiguration(Class<T> serializeType, @Nullable final Logger logger) {
		final SerializerRegistry<T> registry = SerializerRegistryRegistry.getRegistryFor(serializeType);
		final Field[] configurableFields = getInheritedFields(this.getClass());
		final Map<String, T> serializeMap = new TreeMap<String, T>(); // for ordering
		for (final Field configurableField : configurableFields) {
			final int modifiers = configurableField.getModifiers();
			if (isFinal(modifiers) || isTransient(modifiers)) continue; // this field cannot be saved
			final Object configurableFieldValue = getField(configurableField, this);
			if (configurableFieldValue == null) continue; // no value to store
			final ISerializer<T, ?> serializer = registry.get(configurableField.getType());
			if (serializer == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + configurableField.getType().getName() + "\"!");
				continue;
			}
			// We don't need to validate this as there can only be fields with the ConfigurableField in the array returned by #getInheritedFieldsWithAnnotation
			final fun.bb1.config.annotations.Configurable configurableFieldAnnotation = configurableField.getAnnotation(fun.bb1.config.annotations.Configurable.class);
			final String nameToSaveUnder = configurableFieldAnnotation!=null && configurableFieldAnnotation.value().isEmpty() ? configurableField.getName() : configurableFieldAnnotation.value();
			if (serializeMap.containsKey(nameToSaveUnder)) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" is repeated!");
				continue;
			}
			final Object serializedConfigurableFieldValue = invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), serializer, configurableFieldValue);
			final T castSerializedConfigurableFieldValue = handle(()-> (T) serializedConfigurableFieldValue);
			if (castSerializedConfigurableFieldValue == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to serialize \"" + nameToSaveUnder + "\"!");
				continue;
			}
			if (configurableFieldAnnotation != null) {
				if (configurableFieldAnnotation.commentPrefix().isEmpty()) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The comment prefix for \"" + nameToSaveUnder + "\" is blank!");
				} else if (!configurableFieldAnnotation.comment().isEmpty()) {
					serializeMap.put(configurableFieldAnnotation.commentPrefix() + nameToSaveUnder, (T) invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), registry.get(String.class), configurableFieldAnnotation.comment()));
				}
			}
			serializeMap.put(nameToSaveUnder, castSerializedConfigurableFieldValue);
		}
		return registry.convertMap(serializeMap);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void deserializeFromConfiguration(Class<T> serializeType, @NotNull final T configuration, @Nullable final Logger logger) {
		final SerializerRegistry<T> registry = SerializerRegistryRegistry.getRegistryFor(serializeType);
		final Map<String, T> serializeMap = registry.convertMap(configuration);
		final Field[] configurableFields = getInheritedFields(this.getClass());
		if (configurableFields.length == 0) return; // no fields to set
		for (final Field configurableField : configurableFields) {
			final int modifiers = configurableField.getModifiers();
			if (isFinal(modifiers) || isTransient(modifiers)) continue; // this field cannot be saved
			final fun.bb1.config.annotations.Configurable configurableFieldAnnotation = configurableField.getAnnotation(fun.bb1.config.annotations.Configurable.class);
			final String nameToSaveUnder = configurableFieldAnnotation!=null && configurableFieldAnnotation.value().isEmpty() ? configurableField.getName() : configurableFieldAnnotation.value();
			if (!serializeMap.containsKey(nameToSaveUnder)) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" was not found in the configuration");
				continue;
			}
			final ISerializer<T, ?> serializer = registry.get(configurableField.getType());
			if (serializer == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + configurableField.getType().getName() + "\"!");
				continue;
			}
			final Object deserializedValue = serializer.deserialize(serializeMap.get(nameToSaveUnder)); // invokeMethod(handle(()->ISerializer.class.getMethod("deserialize", Object.class)), serializer, serializeMap.get(nameToSaveUnder));
			if (deserializedValue == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to deserialize \"" + nameToSaveUnder + "\"!");
				continue;
			}
			setField(configurableField, this, deserializedValue);
		}
	}
	/**
	 * @apiNote Due to deprecation this will not be checked for
	 * 
	 * @deprecated This is not needed as all fields are serialized
	 */
	@Override
	@Deprecated
	public final @Nullable Map<String, Object> getExtraConfigurables() {
		return null;
	}
	/**
	 * @apiNote Due to deprecation this will not be checked for
	 * 
	 * @deprecated This is not needed as all fields are serialized
	 */
	@Override
	@Deprecated
	public @Nullable Map<String, Class<?>> getExtraConfigurablesTypes() {
		return null;
	}
	/**
	 * @apiNote Due to deprecation this will not be checked for
	 * 
	 * @deprecated This is not needed as all fields are serialized
	 */
	@Override
	@Deprecated
	public void setExtraConfigurables(final @NotNull Map<String, Object> extras) { }
	
}
