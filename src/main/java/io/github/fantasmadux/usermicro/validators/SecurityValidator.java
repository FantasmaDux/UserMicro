package io.github.fantasmadux.usermicro.validators;

import io.github.fantasmadux.usermicro.api.exceptions.CodeExpiredException;
import io.github.fantasmadux.usermicro.api.exceptions.InvalidCodeException;
import io.github.fantasmadux.usermicro.store.entities.EmailUpdateSessionEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class SecurityValidator {
    private static final Logger log = LoggerFactory.getLogger(SecurityValidator.class);
    private final PasswordEncoder passwordEncoder;

    public String getTrimmedCodeOrThrow(String code) {
        if (code == null || code.trim().isEmpty()) {
            log.error("Код не задан.");
            throw new InvalidCodeException();
        }
        return code.trim();
    }

    public void checkIfCodeIsValid(EmailUpdateSessionEntity session, String code) {
        if (code == null || code.isBlank() || !passwordEncoder.matches(code, session.getCode())) {
            log.error("Код невалидный.");
            throw new InvalidCodeException();
        }
    }

    public void ensureCodeIsNotExpired(EmailUpdateSessionEntity session) {
        if (session.getCodeExpires().before(new Timestamp(System.currentTimeMillis()))) {
            log.error("Срок действия кода истек");
            throw new CodeExpiredException();
        }
    }
}
