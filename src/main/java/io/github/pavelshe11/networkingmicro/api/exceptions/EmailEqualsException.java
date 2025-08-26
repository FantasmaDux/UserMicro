package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailEqualsException extends AbstractException {
    public EmailEqualsException() {
        super("check.email", "handle.error", HttpStatus.BAD_REQUEST, "email");
    }
}
