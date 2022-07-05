package fun.bb1.config.adapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.config.IAnnotatedConfigurable;
import fun.bb1.objects.Primitive;
import fun.bb1.objects.PrimitiveMap;
import fun.bb1.objects.Tuple;

import static fun.bb1.exceptions.handler.ExceptionHandler.handle;

public abstract class AbstractAdapter<T> {
	
	private final @NotNull Class<T> clazz;
	
	protected AbstractAdapter(@NotNull final Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public abstract @Nullable T translate(@NotNull final Primitive primitive);
	/**
	 * Used by {@link IAnnotatedConfigurable} to serialize a map of values alongside its comments
	 * 
	 * @param map The map to be serialized
	 * 
	 * @return The serialized version of the map
	 */
	@Internal
	public abstract @Nullable T translate(@NotNull final Map<String, Tuple<Primitive, String>> map);
	
	public abstract @Nullable PrimitiveMap<String> translate(@NotNull final T map);
	
	public abstract @NotNull String convertToString(@NotNull final T primitive);
	
	public abstract @NotNull T convertFromString(@NotNull final String primitive);
	/**
	 * Writes the provided data to the specified file
	 * 
	 * @param path The path to the file to save to
	 * @param t The data to save
	 * 
	 * @apiNote Forwards to {@link #writeToFile(File, Object)}
	 * 
	 * @return If the data was writ to the file
	 */
	public boolean writeToFile(@NotNull final String path, @NotNull final T t) {
		return this.writeToFile(new File(path), t);
	}
	/**
	 * Writes the provided data to the specified file
	 * 
	 * @param path The file to save to
	 * @param t The data to save
	 * 
	 * @apiNote Forwards to {@link #writeToFile(Path, Object)}
	 * 
	 * @return If the data was writ to the file
	 */
	public boolean writeToFile(@NotNull final File file, @NotNull final T t) {
		return this.writeToFile(file.toPath().toAbsolutePath(), t);
	}
	/**
	 * Writes the provided data to the specified file
	 * 
	 * @param path The path to the file to save to
	 * @param t The data to save
	 * 
	 * @return If the data was writ to the file
	 */
	public boolean writeToFile(@NotNull final Path path, @NotNull final T t) {
		return handle(()->Files.writeString(path, this.convertToString(t))) != null;
	}
	
	public abstract @NotNull String getRecommendedFileExtension();
	
	public final @NotNull Class<T> getAdaptiveClass() {
		return this.clazz;
	}
	
}
