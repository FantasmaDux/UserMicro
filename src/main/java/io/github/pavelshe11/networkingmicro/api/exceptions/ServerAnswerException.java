package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ServerAnswerException extends AbstractException{
    public ServerAnswerException() {
        super("Внутренняя ошибка сервера", HttpStatus.BAD_REQUEST);
    }
}
