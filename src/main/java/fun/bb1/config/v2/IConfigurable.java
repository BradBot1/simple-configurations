package fun.bb1.config.v2;

import static fun.bb1.reflection.FieldUtils.getField;
import static fun.bb1.reflection.FieldUtils.getInheritedFields;
import static fun.bb1.reflection.FieldUtils.setField;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isTransient;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.v2.adapter.AbstractAdapter;
import fun.bb1.config.v2.adapter.AdapterController;
import fun.bb1.config.v2.annotations.Configurable;
import fun.bb1.config.v2.serializer.ISerializer;
import fun.bb1.config.v2.serializer.SerializerController;
import fun.bb1.objects.Primitive;
import fun.bb1.objects.PrimitiveMap;
import fun.bb1.objects.Tuple;

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
 * The base serializer
 * 
 * @author BradBot_1
 */
public interface IConfigurable {
	/**
	 * Serializes the implementor of this interface
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 * @return The serialized form of the data
	 */
	public default <T> @Nullable T serializeForConfiguration(Class<T> serializeType, @Nullable final Logger logger, @NotNull final Predicate<Field> fieldMatcher) {
		final AbstractAdapter<T> adapter = AdapterController.getAdapterFor(serializeType);
		if (adapter == null) {
			if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No adapter found for \"" + serializeType.getName() + "\"!");
			return null;
		}
		final Field[] matchedFields = Set.of(getInheritedFields(this.getClass())).stream().filter((f)->fieldMatcher.test(f))
				  .filter((f)->!isFinal(f.getModifiers()))
				  .filter((f)->!isTransient(f.getModifiers()))
				  .toArray((i)->new Field[i]);
		final @Nullable Map<String, Class<?>> extraConfigurableTypes = this.getExtraConfigurablesTypes();
		if (extraConfigurableTypes == null && matchedFields.length < 1) return null; // nothing to save
		final Map<String, Tuple<Primitive, String>> serializeMap = new HashMap<String, Tuple<Primitive, String>>(); // LinkedHashMap for ordering
		final Set<String> blacklistedKeys = this.getExtraConfigurablesTypes()!=null ? this.getExtraConfigurablesTypes().keySet() : Set.of();
		for (final Field field : matchedFields) {
			final Object fieldValue = getField(field, this);
			if (fieldValue == null) continue; // no value to store
			final ISerializer<?> serializer = SerializerController.getSerializerFor(field.getType());
			if (serializer == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + field.getType().getName() + "\"!");
				continue;
			}
			final @Nullable Configurable fieldAnnotation = field.getAnnotation(Configurable.class);
			final String nameToSaveUnder = fieldAnnotation != null ? fieldAnnotation.value().isEmpty() ? field.getName() : fieldAnnotation.value() : field.getName();
			if (serializeMap.containsKey(nameToSaveUnder)) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" is repeated!");
				continue;
			}
			if (blacklistedKeys.contains(nameToSaveUnder)) continue; // blacklisted
			final Primitive serializedFieldValue = serializer.toObjectSerializer().serialize(fieldValue);
			if (serializedFieldValue == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to serialize \"" + nameToSaveUnder + "\"!");
				continue;
			}
			String comment = null;
			if (fieldAnnotation!=null && !fieldAnnotation.comment().isEmpty()) {
				comment = SerializerController.getSerializerFor(String.class).serialize(fieldAnnotation.comment()).getAsString();
			}
			serializeMap.put(nameToSaveUnder, new Tuple<Primitive, String>(serializedFieldValue, comment));
		}
		final Map<String, Object> extras = this.getExtraConfigurables();
		if (extras != null) {
			for (final Entry<String, Object> entry : extras.entrySet()) {
				final ISerializer<?> serializer = SerializerController.getSerializerFor(entry.getValue().getClass());
				if (serializer == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + entry.getValue().getClass().getName() + "\"!");
					continue;
				}
				serializeMap.put(entry.getKey(), new Tuple<Primitive, String>(SerializerController.serialize(entry.getValue()), null));
			}
		}
		return adapter.translateMap(serializeMap);
	}
	/**
	 * Deserializes the implementor of this interface with the provided serialized object
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param configuration The serialized data to be used
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 */
	public default <T> void deserializeFromConfiguration(Class<T> serializeType, @NotNull final T configuration, @Nullable final Logger logger, @NotNull final Predicate<Field> fieldMatcher) {
		final AbstractAdapter<T> adapter = AdapterController.getAdapterFor(serializeType);
		if (adapter == null) {
			if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No adapter found for \"" + serializeType.getName() + "\"!");
			return;
		}
		final @Nullable Map<String, Class<?>> extraConfigurableTypes = this.getExtraConfigurablesTypes();
		final Set<String> blacklistedKeys = extraConfigurableTypes!=null ? extraConfigurableTypes.keySet() : Set.of();
		final PrimitiveMap<String> serializeMap = adapter.translate(configuration).getAsStringBasedMap();
		if (extraConfigurableTypes != null) {
			final Map<String, Object> extras = new HashMap<String, Object>();
			for (final Entry<String, Class<?>> entry : extraConfigurableTypes.entrySet()) {
				final ISerializer<?> serializer = SerializerController.getSerializerFor(entry.getValue());
				if (serializer == null) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + entry.getValue().getName() + "\"!");
					continue;
				}
				if (!serializeMap.containsKey(entry.getKey())) {
					if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, '"' + entry.getKey() + "\" was requested but is not present in the congiuration!");
					continue;
				}
				extras.put(entry.getKey(), serializer.deserialize(serializeMap.get(entry.getKey())));
			}
			this.setExtraConfigurables(extras);
		}
		final Field[] matchedFields = Set.of(getInheritedFields(this.getClass())).stream().filter((f)->fieldMatcher.test(f))
																						  .filter((f)->!isFinal(f.getModifiers()))
																						  .filter((f)->!isTransient(f.getModifiers()))
																						  .toArray((i)->new Field[i]);
		if (matchedFields.length == 0) return; // no fields to set
		for (final Field field : matchedFields) {
			final @Nullable Configurable fieldAnnotation = field.getAnnotation(Configurable.class);
			final String nameToSaveUnder = fieldAnnotation != null ? fieldAnnotation.value().isEmpty() ? field.getName() : fieldAnnotation.value() : field.getName();
			if (!serializeMap.containsKey(nameToSaveUnder)) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "The key \"" + nameToSaveUnder + "\" was not found in the configuration");
				continue;
			}
			if (blacklistedKeys.contains(nameToSaveUnder)) continue; // blacklisted
			final ISerializer<?> serializer = SerializerController.getSerializerFor(field.getType());
			if (serializer == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "No serializer found for \"" + field.getType().getName() + "\"!");
				continue;
			}
			final Object deserializedValue = serializer.deserialize(serializeMap.get(nameToSaveUnder));
			if (deserializedValue == null) {
				if (logger != null && this.enableConfigurationLogging()) logger.log(Level.WARNING, "Failed to deserialize \"" + nameToSaveUnder + "\"!");
				continue;
			}
			setField(field, this, deserializedValue);
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
	/**
	 * Serializes the implementor of this interface
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 * 
	 * @apiNote Forwards to {@link #serializeForConfiguration(Class, Logger, Predicate)}
	 * 
	 * @return The serialized form of the data
	 */
	public default <T> @Nullable T serializeForConfiguration(Class<T> serializeType, @Nullable final Logger logger) {
		return this.serializeForConfiguration(serializeType, logger, (f)->true);
	}
	/**
	 * Deserializes the implementor of this interface with the provided serialized object
	 * 
	 * @param serializeType The {@link Class} of the serialization type
	 * @param configuration The serialized data to be used
	 * @param logger An optional {@link Logger}, if null nothing is logged, if not null warnings are logged
	 * 
	 * @apiNote Forwards to {@link #deserializeFromConfiguration(Class, Object, Logger, Predicate)}
	 */
	public default <T> void deserializeFromConfiguration(Class<T> serializeType, @NotNull final T configuration, @Nullable final Logger logger) {
		this.deserializeFromConfiguration(serializeType, configuration, logger, (f)->true);
	}
}
