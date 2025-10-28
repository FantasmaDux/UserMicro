package io.github.fantasmadux.usermicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AvatarNotFoundException extends AbstractException{
    public AvatarNotFoundException() {
        super("avatar.not.found", "handle.error", HttpStatus.BAD_REQUEST, "avatar", null);
    }

}
