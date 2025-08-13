package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitutionEntity, UUID> {
    List<EducationalInstitutionEntity> findAllByDomenName(String domenName);
    EducationalInstitutionEntity findByDomenName(String domenName);

    boolean existsByDomenName(String domenName);
}
