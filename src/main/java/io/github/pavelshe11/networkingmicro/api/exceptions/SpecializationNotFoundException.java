package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class SpecializationNotFoundException extends AbstractException{
    public SpecializationNotFoundException() {
        super("specialization.not.found", HttpStatus.BAD_REQUEST);
    }
}
