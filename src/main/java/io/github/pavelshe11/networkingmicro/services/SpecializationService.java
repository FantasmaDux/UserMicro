package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.InstitutionNotFoundException;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    Logger log = LoggerFactory.getLogger(SpecializationService.class);
    private final EducationalInstitutionRepository educationalInstitutionRepository;

    @Transactional
    public SpecializationsByInstitutionDto getSpecializationsByInstitution(UUID institutionId) {

        if (!educationalInstitutionRepository.existsById(institutionId)) {
            log.error("Институт с id {} не найден", institutionId);
            throw new InstitutionNotFoundException();
        }

        Optional<EducationalInstitutionEntity> educationalInstitutionOpt
                = educationalInstitutionRepository.findById(institutionId);

        EducationalInstitutionEntity educationalInstitution = educationalInstitutionOpt.get();

        List<SpecializationsByInstitutionDto.SpecializationByInstitutionDto> specializations =
                educationalInstitution.getInstitutionSpecialties().stream()
                        .map(InstitutionSpecialtiesEntity::getSpecialization)
                        .map(specialization -> SpecializationsByInstitutionDto
                                .SpecializationByInstitutionDto.builder()
                                .id(specialization.getId())
                                .name(specialization.getName())
                                .countOfCourses(specialization.getCountOfCourses())
                                .build())
                        .toList();

        return SpecializationsByInstitutionDto.builder()
                .specializationsByInstitution(specializations)
                .build();
    }
}
