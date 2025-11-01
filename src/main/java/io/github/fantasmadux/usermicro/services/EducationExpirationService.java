package io.github.fantasmadux.usermicro.services;

import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class EducationExpirationService {

    private final Logger log = LoggerFactory.getLogger(EducationExpirationService.class);
    private final AccountRepository accountRepository;

    @Scheduled(cron = "${ACCOUNT_EDUCATION_END_CHECK}")
    @Transactional
    public void courseYearIncrement() {
        log.info("Вызван метод courseYearIncrement()");

        List<AccountEntity> accounts = accountRepository.findAll();

        LocalDate today = LocalDate.now();

        for (AccountEntity account : accounts) {
            LocalDate dateOfEducationEnd = account.getDateOfEducationEnd();

            if (dateOfEducationEnd != null && dateOfEducationEnd.isEqual(today)) {
                log.info("Пользователь с ID {}: на последнем дне обучения.", account.getId());
            }
        }
    }
}
