package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.ActivitySessionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InitiativeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountCleanerService {
    private final ActivitySessionRepository activitySessionRepository;
    private final AccountUpdateService accountUpdateService;
    private static final Logger log = LoggerFactory.getLogger(AccountCleanerService.class);
    private final AccountContactInfoRepository accountContactInfoRepository;
    private final InitiativeRepository initiativeRepository;
    private final AccountRepository accountRepository;

    @Scheduled(fixedRateString = "${DEAD_ACCOUNT_CLEAN_TIME}")
    @Transactional
    protected void cleanInactiveAccounts() {
        long now = new Timestamp(System.currentTimeMillis()).getTime();
        List<ActivitySessionEntity> sessions = activitySessionRepository.findAll();

        for (ActivitySessionEntity session : sessions) {

            long inactivityTimeMs = session.getInactivityTimeMs();
            long lastActivity = session.getLastActivity().getTime();

            if ((lastActivity + inactivityTimeMs) < now) {
                UUID accountId = session.getAccount().getId();

                try {
                    deleteAccountRelationships(accountId);
                    accountUpdateService.deleteAccount(accountId);
                    log.info("Удален аккаунт {}", accountId);
                } catch (Exception e) {
                    log.error("Ошибка удаления аккаунта {}", accountId);
                }
            }

        }
    }

    private void deleteAccountRelationships(UUID accountId) {

        AccountEntity account = accountRepository.findById(accountId).get();

        activitySessionRepository.deleteByAccount(account);

        accountContactInfoRepository.deleteByAccount(account);

        initiativeRepository.updateAccountToNull(accountId);

    }

}
