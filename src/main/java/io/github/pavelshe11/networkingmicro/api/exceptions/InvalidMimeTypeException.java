package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidMimeTypeException extends AbstractException {
    public InvalidMimeTypeException() {
        super("invalid.mime.type", HttpStatus.BAD_REQUEST);
    }
}
