package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.SetSkillsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetSkillsRepository extends JpaRepository<SetSkillsEntity, UUID> {
}
