package io.github.fantasmadux.usermicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactMethodType {
    LINK("link"),
    PHONE("phone"),
    EMAIL("email");

    private String contactMethodType;
}
