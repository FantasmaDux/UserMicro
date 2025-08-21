package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class SetInactivityMonthException extends AbstractException{
    public SetInactivityMonthException() {
        super("set.inactivity.month.exception", HttpStatus.BAD_REQUEST);
    }

}
