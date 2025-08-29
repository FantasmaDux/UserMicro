package io.github.pavelshe11.networkingmicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactVisibilityType {
    PRIVATE("private"),
    PUBLIC("public");

    private String visibilityType;
}
