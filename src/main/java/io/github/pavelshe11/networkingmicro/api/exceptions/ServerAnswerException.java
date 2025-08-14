package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ServerAnswerException extends AbstractException{
    public ServerAnswerException() {
        super("server.inner.error", HttpStatus.BAD_REQUEST);
    }
}
