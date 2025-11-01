package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.MatchWithInitiativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchWithInitiativeRepository extends JpaRepository<MatchWithInitiativeEntity, UUID> {
}
