package io.github.pavelshe11.networkingmicro.util;

import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
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
    private final AccountRepository accountRepository;

    public void updateLastActivitySession(UUID accountId) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        Optional<ActivitySessionEntity> sessionOpt = activitySessionRepository.findByAccount(account);

        if (sessionOpt.isPresent()) {
            ActivitySessionEntity activitySession = sessionOpt.get();
            activitySession.setLastActivity(new Timestamp(System.currentTimeMillis()));
            activitySessionRepository.save(activitySession);
        }

    }
}
