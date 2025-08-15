package io.github.pavelshe11.networkingmicro.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUtil {

    public UUID claimAccountId() {
        Jwt jwt = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return UUID.fromString(jwt.getClaim("accountId"));
    }
}
