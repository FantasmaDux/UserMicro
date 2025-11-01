package io.github.fantasmadux.usermicro.util;

import io.github.fantasmadux.usermicro.api.exceptions.ServerAnswerException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    public UUID claimAccountId() {
        var context = SecurityContextHolder.getContext();
        if (context == null) {
            log.error("SecurityContext - null");
            throw new ServerAnswerException();
        }

        var authentication = context.getAuthentication();
        if (authentication == null) {
            log.error("Authentication - null");
            throw new ServerAnswerException();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            log.error("Передан не JWT");
            throw new ServerAnswerException();

        }

        String accountId = jwt.getClaim("accountId");
        if (accountId == null) {
            log.error("JWT не содержит accountId");
            throw new ServerAnswerException();

        }

        return UUID.fromString(accountId);
    }
}
