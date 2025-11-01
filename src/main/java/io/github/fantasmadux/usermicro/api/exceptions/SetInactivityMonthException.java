package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class SetInactivityMonthException extends AbstractException{
    public SetInactivityMonthException() {
        super("set.inactivity.month.exception", "handle.error", HttpStatus.BAD_REQUEST, "inactivityTimeMs", null);
    }

}
