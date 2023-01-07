package fun.bb1.config.entries;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import fun.bb1.objects.Primitive;
import fun.bb1.objects.annotations.AllowsEmptyString;

@Internal
public record ConfigValue(@NotNull @AllowsEmptyString String comment, @NotNull Primitive value) {}