package fun.bb1.config;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;
import static fun.bb1.reflection.FieldUtils.getField;
import static fun.bb1.reflection.FieldUtils.setField;
import static fun.bb1.reflection.FieldUtils.getInheritedFieldsWithAnnotation;
import static fun.bb1.reflection.MethodUtils.invokeMethod;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isTransient;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.annotations.Configurable;
import fun.bb1.config.serializer.ISerializer;
import fun.bb1.config.serializer.SerializerRegistry;
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
 * An interface to aid with configuration
 * <br>
 * This class looks for the {@link Configurable} annotation and uses that to determine what should be serialized
 * <br>
 * For a whole class to represent a config look to {@link fun.bb1.config.Configurable}
 * 
 * @author BradBot_1
 */
public interface IAnnotatedConfigurable {
	/**
	 * Serializes the implementor of this interface
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 * @return The serialized form of the data
	 */
	@SuppressWarnings("unchecked")
	public default <T> @Nullable T serializeForConfiguration(Class<T> serializeType, @Nullable final Logger logger) {
		final IRegistry<Class<?>, ISerializer<?, ?>> registry = SerializerRegistry.getRegistryFor(serializeType);
		final Field[] configurableFields = getInheritedFieldsWithAnnotation(this.getClass(), Configurable.class);
		final Map<String, Object> serializeMap = new HashMap<String, Object>();
		final Set<String> blacklistedKeys = this.getExtraConfigurablesTypes()!=null ? this.getExtraConfigurablesTypes().keySet() : Set.of();
		if (configurableFields.length > 0) {
			for (final Field configurableField : configurableFields) {
				final int modifiers = configurableField.getModifiers();
				if (isFinal(modifiers) || isTransient(modifiers)) continue; // this field cannot be saved
				final Object configurableFieldValue = getField(configurableField, this);
				if (configurableFieldValue == null) continue; // no value to store
				final ISerializer<?, ?> serializer = registry.get(configurableField.getDeclaringClass());
				if (serializer == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + configurableField.getDeclaringClass().getName() + "\"!");
					continue;
				}
				// We don't need to validate this as there can only be fields with the ConfigurableField in the array returned by #getInheritedFieldsWithAnnotation
				final Configurable configurableFieldAnnotation = configurableField.getAnnotation(Configurable.class);
				final String nameToSaveUnder = configurableFieldAnnotation.value().isEmpty() ? configurableField.getName() : configurableFieldAnnotation.value();
				if (serializeMap.containsKey(nameToSaveUnder)) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" is repeated!");
					continue;
				}
				if (blacklistedKeys.contains(nameToSaveUnder)) continue; // blacklisted
				final Object serializedConfigurableFieldValue = invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), serializer, configurableFieldValue);
				final T castSerializedConfigurableFieldValue = handle(()-> (T) serializedConfigurableFieldValue);
				if (castSerializedConfigurableFieldValue == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to serialize \"" + nameToSaveUnder + "\"!");
					continue;
				}
				if (configurableFieldAnnotation.commentPrefix().isEmpty()) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The comment prefix for \"" + nameToSaveUnder + "\" is blank!");
				} else if (!configurableFieldAnnotation.comment().isEmpty()) {
					serializeMap.put(configurableFieldAnnotation.commentPrefix() + nameToSaveUnder, configurableFieldAnnotation.comment());
				}
				serializeMap.put(nameToSaveUnder, castSerializedConfigurableFieldValue);
			}
		}
		final Map<String, Object> extras = this.getExtraConfigurables();
		if (extras != null) {
			for (final Entry<String, Object> entry : extras.entrySet()) {
				final ISerializer<?, ?> serializer = registry.get(entry.getValue().getClass());
				if (serializer == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + entry.getValue().getClass().getName() + "\"!");
					continue;
				}
				serializeMap.put(entry.getKey(), invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), serializer, entry.getValue()));
			}
		}
		return (T) invokeMethod(handle(()->ISerializer.class.getMethod("serialize", Object.class)), registry.get(Map.class), serializeMap);
	}
	/**
	 * Deserializes the implementor of this interface with the provided serialized object
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param configuration The serialized data to be used
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 */
	public default void deserializeFromConfiguration(Class<?> serializeType, @NotNull final Object configuration, @Nullable final Logger logger) {
		final Map<String, Class<?>> types = this.getExtraConfigurablesTypes();
		final Set<String> blacklistedKeys = types!=null ? this.getExtraConfigurablesTypes().keySet() : Set.of();
		final IRegistry<Class<?>, ISerializer<?, ?>> registry = SerializerRegistry.getRegistryFor(serializeType);
		final Map<String, Object> serializeMap = ((Map<?, ?>)invokeMethod(handle(()->ISerializer.class.getMethod("deserialize", Object.class)), registry.get(Map.class), configuration))
													.entrySet()
													.stream()
													.collect(() -> new HashMap<String, Object>(),
															(t, u) -> t.put(u.getKey() instanceof String s ? s : u.getKey().toString(), u.getValue()),
															(t, u) -> t.putAll(u));
		if (types != null) {
			final Map<String, Object> extras = new HashMap<String, Object>();
			for (final Entry<String, Class<?>> entry : types.entrySet()) {
				final ISerializer<?, ?> serializer = registry.get(entry.getValue());
				if (serializer == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + entry.getValue().getName() + "\"!");
					continue;
				}
				if (!serializeMap.containsKey(entry.getKey())) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, '"' + entry.getKey() + "\" was requested but is not present in the congiuration!");
					continue;
				}
				extras.put(entry.getKey(), invokeMethod(handle(()->ISerializer.class.getMethod("deserialize", Object.class)), serializer, serializeMap.get(entry.getKey())));
			}
			this.setExtraConfigurables(extras);
		}
		final Field[] configurableFields = getInheritedFieldsWithAnnotation(this.getClass(), Configurable.class);
		if (configurableFields.length == 0) return; // no fields to set
		for (final Field configurableField : configurableFields) {
			final int modifiers = configurableField.getModifiers();
			if (isFinal(modifiers) || isTransient(modifiers)) continue; // this field cannot be saved
			final Configurable configurableFieldAnnotation = configurableField.getAnnotation(Configurable.class);
			final String nameToSaveUnder = configurableFieldAnnotation.value().isEmpty() ? configurableField.getName() : configurableFieldAnnotation.value();
			if (!serializeMap.containsKey(nameToSaveUnder)) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" was not found in the configuration");
				continue;
			}
			if (blacklistedKeys.contains(nameToSaveUnder)) continue; // blacklisted
			final ISerializer<?, ?> serializer = registry.get(configurableField.getDeclaringClass());
			if (serializer == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + configurableField.getDeclaringClass().getName() + "\"!");
				continue;
			}
			final Object deserializedValue = invokeMethod(handle(()->ISerializer.class.getMethod("deserialize", Object.class)), serializer, serializeMap.get(nameToSaveUnder));
			if (deserializedValue == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to deserialize \"" + nameToSaveUnder + "\"!");
				continue;
			}
			setField(configurableField, this, deserializedValue);
		}
	}
	/**
	 * @apiNote These take priority over values gathered from the configuration
	 * @return A map of values to be added for serialization
	 */
	public default @Nullable Map<String, Object> getExtraConfigurables() {
		return null;
	}
	/**
	 * @apiNote All values in here will NOT have fields with the corresponding name be serialized
	 * @return A list of extra keys that are reserved for {@link #getExtraConfigurables()}
	 */
	public default @Nullable Map<String, Class<?>> getExtraConfigurablesTypes() {
		final Map<String, Object> extras = getExtraConfigurables();
		if (extras == null) return null;
		return extras.entrySet()
						.stream()
						.collect(() -> new HashMap<String, Class<?>>(),
								(t, u) -> t.put(u.getKey(), u.getValue().getClass()),
								(t, u) -> t.putAll(u));
	}
	/**
	 * Used to apply data from a configuration that was manually added via {@link #getExtraConfigurables()}
	 * 
	 * @param extras A {@link Map} of values that have been deserialized
	 * 
	 * @apiNote If you designate a key in {@link #getExtraConfigurablesKeys()} and do not provide a value in {@link #getExtraConfigurables()} it will not be present in this map
	 */
	public default void setExtraConfigurables(final @NotNull Map<String, Object> extras) { }
	/**
	 * @return If to perform logging
	 */
	public default boolean enableConfigurationLogging() {
		return true;
	}
	
}
