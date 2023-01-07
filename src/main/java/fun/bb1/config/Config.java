package fun.bb1.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.objects.Primitive;
import fun.bb1.objects.annotations.AllowsEmptyString;
import fun.bb1.objects.annotations.DisallowsEmptyString;

public final class Config {
	
	private @Nullable String[] topLevelComments;
	private final @NotNull Map<String, Primitive> root = new ConcurrentHashMap<String, Primitive>();
	
	public final void addValue(@Nullable @AllowsEmptyString final String comment, @NotNull @DisallowsEmptyString final String key, @NotNull final Primitive value) {
		
	}
	
}
