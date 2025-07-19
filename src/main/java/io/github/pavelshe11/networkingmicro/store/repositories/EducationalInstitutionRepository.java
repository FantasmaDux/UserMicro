package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitutionEntity, UUID> {
}
