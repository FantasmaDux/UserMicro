package io.github.fantasmadux.usermicro.util;

import io.github.fantasmadux.usermicro.api.exceptions.ServerAnswerException;
import io.github.fantasmadux.usermicro.services.AccountCleanerService;
import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.entities.ActivitySessionEntity;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import io.github.fantasmadux.usermicro.store.repositories.ActivitySessionRepository;
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
    private final AccountCleanerService accountCleanerService;

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
            accountCleanerService.cleanInactiveAccounts(
                    accountId,
                    activitySession.getLastActivity().getTime() + activitySession.getInactivityTimeMs()
            );
        }

    }
}
