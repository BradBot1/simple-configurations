package fun.bb1.config;

import org.jetbrains.annotations.NotNull;

import fun.bb1.config.serializer.ISerializer;
import fun.bb1.objects.Primitive;

final class CastingSerializer<T> implements ISerializer<T> {
	
	private final @NotNull ISerializer<Object> innerTranslator;
	
	CastingSerializer(@NotNull final ISerializer<Object> innerTranslator) {
		this.innerTranslator = innerTranslator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @NotNull T translate(@NotNull Primitive primitiveForm) {
		return (T) this.innerTranslator.translate(primitiveForm);
	}

	@Override
	public @NotNull Primitive translate(@NotNull T primitiveForm) {
		return this.innerTranslator.translate((Object) primitiveForm);
	}
	
	
	
}
