package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.FilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilterRepository extends JpaRepository<FilterEntity, UUID> {
}
