package io.github.pavelshe11.networkingmicro.util;

import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.ActivitySessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class ActivitySessionUpdaterUtil {
    private final ActivitySessionRepository activitySessionRepository;

    public void updateLastActivitySession(UUID accountId) {
        Optional<ActivitySessionEntity> sessionOpt = activitySessionRepository.findByAccountId(accountId);

        if (sessionOpt.isPresent()) {
            ActivitySessionEntity activitySession = sessionOpt.get();
            activitySession.setLastActivity(new Timestamp(System.currentTimeMillis()));
            activitySessionRepository.save(activitySession);
        }

    }
}
