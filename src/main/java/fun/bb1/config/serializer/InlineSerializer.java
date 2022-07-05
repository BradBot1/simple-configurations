package fun.bb1.config.serializer;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import fun.bb1.objects.Primitive;
import fun.bb1.objects.defineables.IInline;

public final class InlineSerializer<T> implements ISerializer<T>, IInline {
	
	private @NotNull final Function<Primitive, T> translateTo;
	private @NotNull final Function<T, Primitive> translateFrom;
	
	public InlineSerializer(@NotNull final Function<Primitive, T> translateTo, @NotNull final Function<T, Primitive> translateFrom) {
		this.translateTo = translateTo;
		this.translateFrom = translateFrom;
	}
	
	@Override
	public final @NotNull T deserialize(@NotNull final Primitive primitiveForm) {
		return this.translateTo.apply(primitiveForm);
	}

	@Override
	public final @NotNull Primitive serialize(@NotNull final T primitiveForm) {
		return this.translateFrom.apply(primitiveForm);
	}

}
