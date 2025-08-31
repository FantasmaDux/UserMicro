package io.github.pavelshe11.networkingmicro.api.exceptions;

import org.springframework.http.HttpStatus;

public class AvatarLargeSizeException extends AbstractException {
    public AvatarLargeSizeException() {
        super("avatar.too.big", "handle.error", HttpStatus.BAD_REQUEST, "avatar", null);
    }
}
