package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ContactAlreadyExistsException extends AbstractException{
    public ContactAlreadyExistsException() {
        super("error.contact.already.exists", "handle.error", HttpStatus.BAD_REQUEST, "contact", null);
    }
}
