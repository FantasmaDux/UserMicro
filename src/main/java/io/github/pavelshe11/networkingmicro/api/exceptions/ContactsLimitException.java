package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class ContactsLimitException extends AbstractException{
    public ContactsLimitException() {
        super("error.contact.limit", "handle.error", HttpStatus.BAD_REQUEST, "contact", null);
    }
}
