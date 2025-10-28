package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailEqualsException extends AbstractException {
    public EmailEqualsException() {
        super("check.email", "handle.error", HttpStatus.BAD_REQUEST, "email", null);
    }
}
