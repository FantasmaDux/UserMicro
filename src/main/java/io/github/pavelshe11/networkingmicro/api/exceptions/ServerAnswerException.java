package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ServerAnswerException extends HttpStatusException {
    public ServerAnswerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
