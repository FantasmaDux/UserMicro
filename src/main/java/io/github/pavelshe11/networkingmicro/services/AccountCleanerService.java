package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.ActivitySessionRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountCleanerService {
    private final ActivitySessionRepository activitySessionRepository;
    private final AccountUpdateService accountUpdateService;
    private static final Logger log = LoggerFactory.getLogger(AccountCleanerService.class);

    @Scheduled(fixedRateString = "${DEAD_ACCOUNT_CLEAN_TIME}")
    @Transactional
    protected void cleanInactiveAccounts() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<ActivitySessionEntity> sessions = activitySessionRepository.findAll();

        for (ActivitySessionEntity session : sessions) {

            Timestamp lastActivity = session.getLastActivity();
            int months = session.getInactivityMonths();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastActivity);
            calendar.add(Calendar.MONTH, months);
            Timestamp deleteAfter = new Timestamp(calendar.getTimeInMillis());

            if (deleteAfter.before(now)) {
                UUID accountId = session.getAccount().getId();

                try {
                    log.info("Удален аккаунт {}", accountId);
                    accountUpdateService.deleteAccount(accountId);
                } catch (Exception e) {
                    log.error("Ошибка удаления аккаунта {}", accountId);
                }
            }

        }
    }

}
