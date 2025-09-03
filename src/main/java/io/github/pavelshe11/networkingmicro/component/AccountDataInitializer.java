package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.enums.ContactVisibilityType;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountDataInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@communicator.ru";
        boolean adminExists = accountRepository.existsByMainEmailContactContact(adminEmail);
        if (!adminExists) {
            EducationalInstitutionEntity educationalInstitution = educationalInstitutionRepository
                    .findByDomenName("communicator.ru").orElseThrow(() -> new ServerAnswerException());
            if (educationalInstitution == null) {
                throw new IllegalStateException("EducationalInstitution with domenName communicator.ru not found");
            }

            String specializationName = "Физика";
            SpecializationEntity specialization = specializationRepository.findByName(specializationName)
                    .orElseGet(() -> {
                        SpecializationEntity newSpecialization = SpecializationEntity.builder()
                                .name(specializationName)
                                .countOfCourses(5)
                                .build();
                        return specializationRepository.save(newSpecialization);
                    });

            AccountEntity account = AccountEntity.builder()
                    .admin(true)
                    .educationalInstitution(educationalInstitution)
                    .visible(true)
                    .ip("1.1.1.1")
                    .acceptedPrivacyPolicy(true)
                    .acceptedPersonalDataProcessing(true)
                    .specialization(specialization)
                    .build();

            account = accountRepository.save(account);

            AccountContactInfoEntity emailContact = AccountContactInfoEntity.builder()
                    .account(account)
                    .contact(adminEmail)
                    .contactMethod(ContactMethodType.EMAIL)
                    .visibility(ContactVisibilityType.PUBLIC)
                    .modifiable(false)
                    .build();

            account.setMainEmailContact(emailContact);
            account.setAccountContactInfos(List.of(emailContact));

            accountRepository.save(account);
        }
    }
}
