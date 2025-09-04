package io.github.pavelshe11.networkingmicro.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public abstract class AbstractException extends RuntimeException {
    private final String messageCode;
    private final String errorCode;
    private final HttpStatus status;
    private final String fieldName;
    private final UUID objectId;

    public AbstractException(String messageCode, String errorCode, HttpStatus status, String fieldName,
                             UUID objectId) {
        this.errorCode = errorCode;
        this.status = status;
        this.messageCode = messageCode;
        this.fieldName = fieldName;
        this.objectId = objectId;
    }
}