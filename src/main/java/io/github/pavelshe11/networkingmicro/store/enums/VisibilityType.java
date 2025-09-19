package io.github.pavelshe11.networkingmicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisibilityType {
    PRIVATE("private"),
    PUBLIC("public");

    private String visibilityType;
}
