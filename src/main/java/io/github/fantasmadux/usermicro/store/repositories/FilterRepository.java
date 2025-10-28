package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.FilterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilterRepository extends JpaRepository<FilterEntity, UUID> {
}
