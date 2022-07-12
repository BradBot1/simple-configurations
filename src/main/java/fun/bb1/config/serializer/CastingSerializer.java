package fun.bb1.config.serializer;

import org.jetbrains.annotations.NotNull;

import fun.bb1.objects.Primitive;

final class CastingSerializer<T> implements ISerializer<T> {
	
	private final @NotNull ISerializer<Object> innerTranslator;
	
	CastingSerializer(@NotNull final ISerializer<Object> innerTranslator) {
		this.innerTranslator = innerTranslator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @NotNull T deserialize(@NotNull Primitive primitiveForm) {
		return (T) this.innerTranslator.deserialize(primitiveForm);
	}

	@Override
	public @NotNull Primitive serialize(@NotNull T primitiveForm) {
		return this.innerTranslator.serialize((Object) primitiveForm);
	}
	
	
	
}
