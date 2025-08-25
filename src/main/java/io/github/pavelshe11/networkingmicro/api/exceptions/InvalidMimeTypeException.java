package io.github.pavelshe11.networkingmicro.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidMimeTypeException extends RuntimeException {
    private final String fieldName;
    private final HttpStatus status;
    private final String errorCode;
    private final String messageCode;
    private final Object[] messageArgs;

    public InvalidMimeTypeException(String extension, String fieldName) {
        super();

        this.fieldName = fieldName;
        this.status = HttpStatus.BAD_REQUEST;

        this.errorCode = "handle.error";
        this.messageCode = "invalid.mime.type";
        this.messageArgs = new Object[]{extension};
    }
}