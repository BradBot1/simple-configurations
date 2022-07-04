package fun.bb1.config;

import org.jetbrains.annotations.NotNull;

import fun.bb1.config.translator.ITranslator;
import fun.bb1.objects.Primitive;

final class CastingTranslator<T> implements ITranslator<T> {
	
	private final @NotNull ITranslator<Object> innerTranslator;
	
	CastingTranslator(@NotNull final ITranslator<Object> innerTranslator) {
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
