package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.SetSkillsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetSkillsRepository extends JpaRepository<SetSkillsEntity, UUID> {
}
