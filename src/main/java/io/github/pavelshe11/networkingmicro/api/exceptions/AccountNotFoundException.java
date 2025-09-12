package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends HttpStatusException{
    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
