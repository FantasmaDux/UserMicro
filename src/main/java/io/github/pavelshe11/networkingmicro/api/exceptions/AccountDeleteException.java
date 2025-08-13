package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AccountDeleteException extends AbstractException{
    public AccountDeleteException() {
        super("Ошибка удаления аккаунта", HttpStatus.BAD_REQUEST);
    }
}
