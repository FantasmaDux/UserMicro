package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.EducationalInstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EducationalInstitutionRepository extends JpaRepository<EducationalInstitutionEntity, UUID> {
    Optional<EducationalInstitutionEntity> findByDomenName(String domenName);

    boolean existsByDomenName(String domenName);
}
