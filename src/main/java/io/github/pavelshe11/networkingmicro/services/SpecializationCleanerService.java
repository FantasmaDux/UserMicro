package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InstitutionSpecialtiesRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpecializationCleanerService {
    private static final Logger log = LoggerFactory.getLogger(SpecializationCleanerService.class);
    private final InstitutionSpecialtiesRepository institutionSpecialtiesRepository;
    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;

    /*
    Удаление специализаций из InstitutionSpecialtiesEntity, к которым никто из студентов не прикреплён,
    и создание актуального списка специализаций. Каждую зиму 1 декабря (01.11.*)
     */
    @Scheduled(cron = "${ACTUALIZE_INSTITUTION_SPECIALITIES_TIME}")
    @Transactional
    protected void actualizeInstitutionSpecialties() {
        cleanInstitutionSpecialties();
        updateInstitutionSpecialties();
    }

    private void cleanInstitutionSpecialties() {
        log.info("Начало удаления специализаций");

        List<InstitutionSpecialtiesEntity> unusedSpecialties =
                institutionSpecialtiesRepository.findAllUnusedSpecialties();

        log.info("Найдено {} неиспользуемых связей", unusedSpecialties.size());

        institutionSpecialtiesRepository.deleteAll(unusedSpecialties);

        log.info("Завершение удаления специализаций");
    }

    private void updateInstitutionSpecialties() {
        log.info("Начало обновления специализаций");

        List<Object[]> activeInstitutionSpecializations =
                accountRepository.findActiveInstitutionSpecializations();

        for (Object[] record : activeInstitutionSpecializations) {
            UUID institutionId = (UUID) record[0];
            UUID specializationId = (UUID) record[1];

            boolean exists = institutionSpecialtiesRepository
                    .existsByEducationalInstitutionIdAndSpecializationId(institutionId, specializationId);

            if (!exists) {
                EducationalInstitutionEntity institution = educationalInstitutionRepository.findById(institutionId).orElse(null);
                SpecializationEntity specialization = specializationRepository.findById(specializationId).orElse(null);

                if (institution != null && specialization != null) {
                    InstitutionSpecialtiesEntity newRelation = InstitutionSpecialtiesEntity.builder()
                            .educationalInstitution(institution)
                            .specialization(specialization)
                            .build();

                    institutionSpecialtiesRepository.save(newRelation);
                }
            }
        }
        log.info("Завершение обновления специализаций");
    }

}

