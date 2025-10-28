package io.github.fantasmadux.usermicro.component;

import io.github.fantasmadux.usermicro.store.entities.EducationalInstitutionEntity;
import io.github.fantasmadux.usermicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class EducationalInstitutionDataInitializer implements ApplicationRunner {

    private final EducationalInstitutionRepository educationalInstitutionRepository;

    @Override
    public void run(ApplicationArguments args) {
        String domen1 = "communicator.ru";
        String domen2 = "test.com";
        boolean exists1 = educationalInstitutionRepository.existsByDomenName(domen1);
        boolean exists2 = educationalInstitutionRepository.existsByDomenName(domen2);

        if (!exists1) {
            EducationalInstitutionEntity educationalInstitution = EducationalInstitutionEntity.builder()
                    .name("Коммуникатор")
                    .domenName(domen1)
                    .build();
            educationalInstitutionRepository.save(educationalInstitution);
        }

        if (!exists2) {
            EducationalInstitutionEntity educationalInstitution = EducationalInstitutionEntity.builder()
                    .name("Тестовый универ")
                    .domenName(domen2)
                    .build();
            educationalInstitutionRepository.save(educationalInstitution);
        }
    }
}
