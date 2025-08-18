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
        boolean specializationExists = specializationRepository.existsByName(specializationName);
        if (!specializationExists) {
            SpecializationEntity specialization = SpecializationEntity.builder()
                    .name(specializationName)
                    .countOfCourses(3)
                    .build();
            specializationRepository.save(specialization);
        }
    }
}
