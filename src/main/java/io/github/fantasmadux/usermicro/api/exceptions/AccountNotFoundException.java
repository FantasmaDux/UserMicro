package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends HttpStatusException{
    public AccountNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }
}
