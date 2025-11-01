package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class CityNotFoundException extends AbstractException {
    public CityNotFoundException() {
        super("city.not.found", "handle.error", HttpStatus.BAD_REQUEST, "idCity", null);
    }

}
