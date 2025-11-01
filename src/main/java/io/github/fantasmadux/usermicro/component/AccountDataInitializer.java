package io.github.fantasmadux.usermicro.component;

import io.github.fantasmadux.usermicro.api.exceptions.ServerAnswerException;
import io.github.fantasmadux.usermicro.store.entities.AccountContactInfoEntity;
import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.entities.EducationalInstitutionEntity;
import io.github.fantasmadux.usermicro.store.entities.SpecializationEntity;
import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import io.github.fantasmadux.usermicro.store.repositories.EducationalInstitutionRepository;
import io.github.fantasmadux.usermicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
@DependsOn("specializationDataInitializer")
public class AccountDataInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;
    private static final Logger log = LoggerFactory.getLogger(AccountDataInitializer.class);

    @Override
    public void run(ApplicationArguments args) {
        log.info("AccountDataInitializer запущен");

        String adminEmail = "admin@communicator.ru";
        boolean adminExists = accountRepository.existsByMainEmailContactContact(adminEmail);
        log.info("Admin exists: {}", adminExists);

        if (!adminExists) {
            try {
                Faker faker = new Faker(new Locale("ru"));
                Random random = new Random();
                List<SpecializationEntity> allSpecializations = specializationRepository.findAll();

                if (allSpecializations.isEmpty()) {
                    log.error("Нет доступных специализаций.");
                    return;
                }

                EducationalInstitutionEntity educationalInstitution = educationalInstitutionRepository
                        .findByDomenName("communicator.ru").orElseThrow(() -> new ServerAnswerException());
                if (educationalInstitution == null) {
                    throw new IllegalStateException("EducationalInstitution with domenName communicator.ru not found");
                }

                int countOfFakers = 500;
                for (int i = 0; i < countOfFakers; i++) {
                    String firstName = faker.name().firstName();
                    String lastName = faker.name().lastName();
                    String middleName = faker.name().lastName();
                    String email = (firstName + "." + lastName + i + "@communicator.ru").toLowerCase();
                    String ip = faker.internet().ipV4Address();

                    SpecializationEntity specialization = allSpecializations.get(random.nextInt(allSpecializations.size()));

                    AccountEntity account = AccountEntity.builder()
                            .admin(false)
                            .firstName(firstName)
                            .lastName(lastName)
                            .middleName(middleName)
                            .educationalInstitution(educationalInstitution)
                            .networking(true)
                            .cityVisible(VisibilityType.PUBLIC)
                            .dateOfBirthVisible(VisibilityType.PUBLIC)
                            .ip(ip)
                            .acceptedPrivacyPolicy(true)
                            .acceptedPersonalDataProcessing(true)
                            .specialization(specialization)
                            .build();

                    account = accountRepository.save(account);
                    log.info("Создание фейкового аккаунта: {} {}", firstName, lastName);

                    AccountContactInfoEntity emailContact = AccountContactInfoEntity.builder()
                            .account(account)
                            .contact(email)
                            .contactMethod(ContactMethodType.EMAIL)
                            .visibility(VisibilityType.PUBLIC)
                            .modifiable(true)
                            .build();

                    account.setMainEmailContact(emailContact);
                    account.setAccountContactInfos(List.of(emailContact));

                    accountRepository.save(account);
                    log.info("Создан аккаунт с email: {}", email);
                }
            } catch (Exception e) {
                log.error("Ошибка добавления юзеров", e);
            }
        }

        if (!adminExists) {
            EducationalInstitutionEntity educationalInstitution = educationalInstitutionRepository
                    .findByDomenName("communicator.ru").orElseThrow(() -> new ServerAnswerException());
            if (educationalInstitution == null) {
                throw new IllegalStateException("EducationalInstitution with domenName communicator.ru not found");
            }

            String specializationName = "Физика";
            SpecializationEntity adminSpecialization = specializationRepository.findByName(specializationName)
                    .orElseGet(() -> {
                        SpecializationEntity newSpecialization = SpecializationEntity.builder()
                                .name(specializationName)
                                .build();
                        return specializationRepository.save(newSpecialization);
                    });

            AccountEntity adminAccount = AccountEntity.builder()
                    .admin(true)
                    .educationalInstitution(educationalInstitution)
                    .networking(true)
                    .cityVisible(VisibilityType.PRIVATE)
                    .dateOfBirthVisible(VisibilityType.PRIVATE)
                    .ip("1.1.1.1")
                    .acceptedPrivacyPolicy(true)
                    .acceptedPersonalDataProcessing(true)
                    .specialization(adminSpecialization)
                    .build();

            adminAccount = accountRepository.save(adminAccount);

            AccountContactInfoEntity adminEmailContact = AccountContactInfoEntity.builder()
                    .account(adminAccount)
                    .contact(adminEmail)
                    .contactMethod(ContactMethodType.EMAIL)
                    .visibility(VisibilityType.PUBLIC)
                    .modifiable(false)
                    .build();

            adminAccount.setMainEmailContact(adminEmailContact);
            adminAccount.setAccountContactInfos(List.of(adminEmailContact));

            accountRepository.save(adminAccount);
        }

    }
}
