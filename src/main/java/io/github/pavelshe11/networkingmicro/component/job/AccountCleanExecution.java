package io.github.pavelshe11.networkingmicro.component.job;

import io.github.pavelshe11.networkingmicro.services.AccountCleanerService;
import io.github.pavelshe11.networkingmicro.services.AccountUpdateService;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.ActivitySessionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InitiativeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountCleanExecution {
    private final AccountUpdateService accountUpdateService;
    private final ActivitySessionRepository activitySessionRepository;
    private final AccountContactInfoRepository accountContactInfoRepository;
    private final InitiativeRepository initiativeRepository;
    private final AccountRepository accountRepository;

    private static final Logger log = LoggerFactory.getLogger(AccountCleanExecution.class);

    @Transactional
    public void clean(UUID accountId) {
        AccountEntity account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            log.warn("Аккаунт {} не найден для удаления", accountId);
            return;
        }

        updateAccountRelationships(accountId);
        accountUpdateService.deleteAccount(accountId);

        log.info("Аккаунт {} успешно удалён", accountId);
    }


    private void updateAccountRelationships(UUID accountId) {

        AccountEntity account = accountRepository.findById(accountId).get();

        activitySessionRepository.deleteByAccount(account);

        accountContactInfoRepository.deleteByAccount(account);

        initiativeRepository.updateAccountToNull(accountId);

    }
}
