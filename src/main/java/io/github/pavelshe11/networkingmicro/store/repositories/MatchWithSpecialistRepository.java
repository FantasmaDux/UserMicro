package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.MatchWithSpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchWithSpecialistRepository extends JpaRepository<MatchWithSpecialistEntity, UUID> {
}
