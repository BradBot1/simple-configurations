package fun.bb1.config.translator;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import fun.bb1.objects.Primitive;
import fun.bb1.objects.defineables.IInline;

public final class InlineTranslator<T> implements ITranslator<T>, IInline {
	
	private @NotNull final Function<Primitive, T> translateTo;
	private @NotNull final Function<T, Primitive> translateFrom;
	
	public InlineTranslator(@NotNull final Function<Primitive, T> translateTo, @NotNull final Function<T, Primitive> translateFrom) {
		this.translateTo = translateTo;
		this.translateFrom = translateFrom;
	}
	
	@Override
	public final @NotNull T translate(@NotNull final Primitive primitiveForm) {
		return this.translateTo.apply(primitiveForm);
	}

	@Override
	public final @NotNull Primitive translate(@NotNull final T primitiveForm) {
		return this.translateFrom.apply(primitiveForm);
	}

}
