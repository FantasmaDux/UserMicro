package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class SpecializationNotFoundException extends AbstractException{
    public SpecializationNotFoundException() {
        super("specialization.not.found", "handle.error", HttpStatus.BAD_REQUEST, "idSpecialization", null);
    }
}
