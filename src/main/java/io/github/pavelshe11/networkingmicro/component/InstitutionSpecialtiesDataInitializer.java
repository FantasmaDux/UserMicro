package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InstitutionSpecialtiesRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class InstitutionSpecialtiesDataInitializer implements ApplicationRunner {
    private final SpecializationRepository specializationRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final InstitutionSpecialtiesRepository institutionSpecialtiesRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String domen = "communicator.ru";
        String[] specializationNames = {"ИТ", "ИБ", "ИИ"};


        Optional<EducationalInstitutionEntity> institutionOpt =
                educationalInstitutionRepository.findByDomenName(domen);

        if (institutionOpt.isEmpty()) {
            return;
        }

        EducationalInstitutionEntity institution = institutionOpt.get();

        for (String specializationName : specializationNames) {
            Optional<SpecializationEntity> specOpt = specializationRepository.findByName(specializationName);

            if (specOpt.isEmpty()) {
                continue;
            }

            SpecializationEntity specialization = specOpt.get();

            boolean alreadyLinked = institutionSpecialtiesRepository
                    .existsByEducationalInstitutionAndSpecialization(institution, specialization);

            if (!alreadyLinked) {
                InstitutionSpecialtiesEntity link = InstitutionSpecialtiesEntity.builder()
                        .educationalInstitution(institution)
                        .specialization(specialization)
                        .build();

                institutionSpecialtiesRepository.save(link);
            }
        }
    }
}
