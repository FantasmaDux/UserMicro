package io.github.fantasmadux.usermicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VisibilityType {
    PRIVATE("private"),
    PUBLIC("public");

    private String visibilityType;
}
