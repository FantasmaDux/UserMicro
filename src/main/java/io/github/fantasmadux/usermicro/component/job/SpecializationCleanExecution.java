package io.github.fantasmadux.usermicro.component.job;

import io.github.fantasmadux.usermicro.store.entities.EducationalInstitutionEntity;
import io.github.fantasmadux.usermicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.fantasmadux.usermicro.store.entities.SpecializationEntity;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import io.github.fantasmadux.usermicro.store.repositories.EducationalInstitutionRepository;
import io.github.fantasmadux.usermicro.store.repositories.InstitutionSpecialtiesRepository;
import io.github.fantasmadux.usermicro.store.repositories.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SpecializationCleanExecution {
    private static final Logger log = LoggerFactory.getLogger(SpecializationCleanExecution.class);

    private final InstitutionSpecialtiesRepository institutionSpecialtiesRepository;
    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final SpecializationRepository specializationRepository;

    /*
    Удаление специализаций из InstitutionSpecialtiesEntity, к которым никто из студентов не прикреплён,
    и создание актуального списка специализаций. Каждую зиму 1 декабря (01.11.*)
     */
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
