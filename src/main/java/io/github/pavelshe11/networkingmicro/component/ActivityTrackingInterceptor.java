package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.util.ActivitySessionUpdaterUtil;
import io.github.pavelshe11.networkingmicro.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ActivityTrackingInterceptor implements HandlerInterceptor {

    private final ActivitySessionUpdaterUtil activitySessionUpdaterUtil;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(ActivityTrackingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            UUID accountId = jwtUtil.claimAccountId();

            if (accountId != null) {
                activitySessionUpdaterUtil.updateLastActivitySession(accountId);
            }

        } catch (Exception ex) {
            log.error("Не удалось обновить активность пользователя ", ex);
        }
        return true;
    }
}
