package io.github.pavelshe11.networkingmicro.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CursorDestinationType {
    BEFORE("before"),
    AFTER("after");

    private final String cursorDestinationType;

    public boolean isBefore() {
        return this == BEFORE;
    }

    public boolean isAfter() {
        return this == AFTER;
    }
}
