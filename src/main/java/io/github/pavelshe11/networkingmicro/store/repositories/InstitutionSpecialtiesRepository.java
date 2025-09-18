package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InstitutionSpecialtiesRepository extends JpaRepository<InstitutionSpecialtiesEntity, UUID> {

    boolean existsByEducationalInstitutionAndSpecialization(EducationalInstitutionEntity institution, SpecializationEntity specialization);

    boolean existsByEducationalInstitutionIdAndSpecializationId(UUID id, UUID specializationId);

    @Query(value = """
            SELECT * FROM institution_specialties isp
            WHERE NOT EXISTS (
                            SELECT 1 FROM account
                            WHERE account.specialization_id = isp.specialization_id
                            AND account.educational_institution_id = isp.educational_institution_id
                        )
            """, nativeQuery = true
    )
    List<InstitutionSpecialtiesEntity> findAllUnusedSpecialties();
}
