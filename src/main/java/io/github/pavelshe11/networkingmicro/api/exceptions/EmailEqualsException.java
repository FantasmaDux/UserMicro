package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class EmailEqualsException extends AbstractException {
    public EmailEqualsException() {
        super("Проверьте указанную почту", HttpStatus.BAD_REQUEST);
    }
}
