package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.InstitutionNotFoundException;
import io.github.pavelshe11.networkingmicro.api.exceptions.SpecializationNotFoundException;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InstitutionSpecialtiesRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    Logger log = LoggerFactory.getLogger(SpecializationService.class);
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;
    private final InstitutionSpecialtiesRepository institutionSpecialtiesRepository;

    @Transactional
    public SpecializationsByInstitutionDto getSpecializationsByInstitution(UUID institutionId) {

        if (!educationalInstitutionRepository.existsById(institutionId)) {
            log.error("Институт с id {} не найден", institutionId);
            throw new InstitutionNotFoundException();
        }

        Optional<EducationalInstitutionEntity> educationalInstitutionOpt
                = educationalInstitutionRepository.findById(institutionId);

        EducationalInstitutionEntity educationalInstitution = educationalInstitutionOpt.get();

        List<SpecializationsDto> specializations =
                educationalInstitution.getInstitutionSpecialties().stream()
                        .map(InstitutionSpecialtiesEntity::getSpecialization)
                        .map(specialization ->
                        {
                            String fullCode = specialization.getSpecializationCode();
                            String trimmedCode = fullCode.contains(".")
                                    ? fullCode.substring(fullCode.indexOf('.') + 1)
                                    : fullCode;

                            return SpecializationsDto.builder()
                                    .id(specialization.getId())
                                    .code(trimmedCode)
                                    .name(specialization.getName())
                                    .build();
                        })
                        .toList();

        return SpecializationsByInstitutionDto.builder()
                .specializationsByInstitution(specializations)
                .build();
    }

    public Slice<SpecializationsDto> getSpecializations(UUID institutionId,
                                                                     String keyword,
                                                                     String cursor, int size) {
        String cursorName = null;
        UUID cursorId = null;

        if (cursor != null && !cursor.isEmpty()) {
            String decoded = new String(Base64.getDecoder().decode(cursor));
            String[] parts = decoded.split(",", 2);
            cursorName = parts[0];
            cursorId = UUID.fromString(parts[1]);
        }

        List<Object[]> rows = specializationRepository.findSpecializationsWithKeysetPagination(
                institutionId,
                keyword,
                cursorName,
                cursorId,
                size + 1
        );

        boolean hasNext = rows.size() > size;
        if (hasNext) {
            rows = rows.subList(0, size);
        }

        List<SpecializationsDto> result = rows.stream()
                .map(row -> new SpecializationsDto(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2]
                ))
                .collect(Collectors.toList());

        return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);

    }

    public void createSpecializationInstitutionRelation(UUID educational_institution_id,
                                                        UUID specialization_id) {

        Optional<EducationalInstitutionEntity> educationalInstitution =
                educationalInstitutionRepository.findById(educational_institution_id);

        if (educationalInstitution.isEmpty()) {
            throw new InstitutionNotFoundException();
        }

        Optional<SpecializationEntity> specialization =
                specializationRepository.findById(specialization_id);

        if (specialization.isEmpty()) {
            throw new SpecializationNotFoundException();
        }

        InstitutionSpecialtiesEntity institutionSpecialties = InstitutionSpecialtiesEntity.builder()
                .educationalInstitution(educationalInstitution.get())
                .specialization(specialization.get())
                .build();

        institutionSpecialtiesRepository.save(institutionSpecialties);
    }
}
