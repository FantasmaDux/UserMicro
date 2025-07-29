package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EducationalInstitutionDataInitializer implements ApplicationRunner {

    private final EducationalInstitutionRepository educationalInstitutionRepository;

    @Override
    public void run(ApplicationArguments args) {
        EducationalInstitutionEntity educationalInstitution = EducationalInstitutionEntity.builder()
                .name("Коммуникатор")
                .domenName("communicator.ru")
                .build();

        educationalInstitutionRepository.save(educationalInstitution);
    }
}
