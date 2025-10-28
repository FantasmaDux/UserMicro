package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class InstitutionNotFoundException extends AbstractException{
    public InstitutionNotFoundException() {
        super("institution.not.found", "handle.error", HttpStatus.BAD_REQUEST, "idInstitution", null);
    }
}
