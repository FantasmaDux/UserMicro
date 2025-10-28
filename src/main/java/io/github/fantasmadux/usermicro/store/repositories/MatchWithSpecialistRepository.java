package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.MatchWithSpecialistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchWithSpecialistRepository extends JpaRepository<MatchWithSpecialistEntity, UUID> {
}
