package com.github.zimablue.attrsystem.utils.read;

import org.jetbrains.annotations.NotNull;

public interface Parser<T> {
    @NotNull
    Result<T> parse(String text);
}
