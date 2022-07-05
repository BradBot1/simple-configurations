package fun.bb1.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fun.bb1.objects.Primitive;
import fun.bb1.registry.IRegistry;
import fun.bb1.registry.SimpleRegistry;

public final class AdapterController {
	
	private static final @NotNull IRegistry<Class<?>, AbstractAdapter<?>> ADAPTER_REGISTRY = new SimpleRegistry<Class<?>, AbstractAdapter<?>>();
	
	public static final @NotNull IRegistry<Class<?>, AbstractAdapter<?>> getAdapterRegistry() {
		return ADAPTER_REGISTRY;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T> @Nullable AbstractAdapter<T> getAdapterFor(@NotNull final Class<T> type) {
		return (AbstractAdapter<T>) getAdapterRegistry().get(type);
	}
	
	public static final <T> @Nullable T translate(@NotNull final Class<T> type, @NotNull final Primitive primitive) {
		return getAdapterFor(type).translate(primitive);
	}
		
}
