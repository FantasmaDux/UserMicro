package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InstitutionSpecialtiesEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstitutionSpecialtiesRepository extends JpaRepository<InstitutionSpecialtiesEntity, UUID> {

    boolean existsByEducationalInstitutionAndSpecialization(EducationalInstitutionEntity institution, SpecializationEntity specialization);
}
