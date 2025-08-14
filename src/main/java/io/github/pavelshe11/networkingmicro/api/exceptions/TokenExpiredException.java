package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AbstractException{
    public TokenExpiredException() {
        super("error.token.expired", HttpStatus.UNAUTHORIZED);
    }
}