package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.services.AccountUpdateService;
import io.github.pavelshe11.networkingmicro.util.ActivitySessionUpdaterUtil;
import io.github.pavelshe11.networkingmicro.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ActivityTrackingFilter extends OncePerRequestFilter {

    private final ActivitySessionUpdaterUtil activitySessionUpdaterUtil;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(ActivityTrackingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            UUID accountId = jwtUtil.claimAccountId();

            if (accountId != null) {
                activitySessionUpdaterUtil.updateLastActivitySession(accountId);
            }

        } catch (Exception ex) {
            log.error("Не удалось обновить активность пользователя");
        }

        filterChain.doFilter(request, response);
    }
}
