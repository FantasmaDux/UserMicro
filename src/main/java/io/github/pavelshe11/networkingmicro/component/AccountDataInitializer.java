package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDataInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@communicator.ru";
        boolean adminExists = accountRepository.existsByEmail(adminEmail);
        if (!adminExists) {
            EducationalInstitutionEntity educationalInstitution =
                    educationalInstitutionRepository.findByDomenName("communicator.ru");
            if (educationalInstitution == null) {
                throw new IllegalStateException("EducationalInstitution with domenName communicator.ru not found");
            }

            AccountEntity account = AccountEntity.builder()
                    .email("admin@communicator.ru")
                    .admin(true)
                    .educationalInstitution(educationalInstitution)
                    .visible(true)
                    .ip("1.1.1.1")
                    .acceptedPrivacyPolicy(true)
                    .acceptedPersonalDataProcessing(true)
                    .build();
            accountRepository.save(account);
        }
    }
}
