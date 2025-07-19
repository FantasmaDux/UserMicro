package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.SetTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetTagsRepository extends JpaRepository<SetTagsEntity, UUID> {
}
