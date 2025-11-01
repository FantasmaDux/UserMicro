package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.SkillNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillNodeRepository extends JpaRepository<SkillNodeEntity, UUID> {
}
