package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class CityNotFoundException extends AbstractException {
    public CityNotFoundException() {
        super("city.not.found", HttpStatus.BAD_REQUEST);
    }

}
