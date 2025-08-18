package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AccountDeleteException extends AbstractException {
    public AccountDeleteException() {
        super("delete.account.exception", HttpStatus.BAD_REQUEST);
    }
}
