package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseIncrementService {

    private final Logger log = LoggerFactory.getLogger(CourseIncrementService.class);
    private final AccountRepository accountRepository;

    @Scheduled(fixedRateString = "${INCREMENT_COURSE_TIME}")
    @Transactional
    protected void courseYearIncrement() {
        List<AccountEntity> accounts = accountRepository.findAll();

        for (AccountEntity account : accounts) {
            short currentCourse = account.getCourseNumber();
            account.setCourseNumber((short) (currentCourse + 1));
            accountRepository.save(account);

            SpecializationEntity specialization = account.getSpecialization();
            int maxCourseNumber = specialization.getCountOfCourses();
            currentCourse = account.getCourseNumber();

            if (currentCourse > maxCourseNumber) {
                // TODO: выполнить действие?
                log.warn("Превышен макимальный курс у аккаунта {}", account.getId());
            }

        }
    }
}
