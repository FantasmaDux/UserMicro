package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecializationDataInitializer implements ApplicationRunner {
    private final SpecializationRepository specializationRepository;

    @Override
    public void run(ApplicationArguments args) {

        String specializationName = "ИТ";
        String specializationName2 = "ИБ";
        String specializationName3 = "ИИ";

        boolean specializationExists = specializationRepository.existsByName(specializationName);
        boolean specializationExists2 = specializationRepository.existsByName(specializationName2);
        boolean specializationExists3 = specializationRepository.existsByName(specializationName3);
        if (!specializationExists) {
            SpecializationEntity specialization = SpecializationEntity.builder()
                    .name(specializationName)
                    .countOfCourses(3)
                    .build();
            specializationRepository.save(specialization);
        }

        if (!specializationExists2) {
            SpecializationEntity specialization = SpecializationEntity.builder()
                    .name(specializationName2)
                    .countOfCourses(4)
                    .build();
            specializationRepository.save(specialization);
        }

        if (!specializationExists3) {
            SpecializationEntity specialization = SpecializationEntity.builder()
                    .name(specializationName3)
                    .countOfCourses(5)
                    .build();
            specializationRepository.save(specialization);
        }
    }
}
