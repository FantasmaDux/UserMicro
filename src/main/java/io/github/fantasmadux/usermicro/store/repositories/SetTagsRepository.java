package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.SetTagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetTagsRepository extends JpaRepository<SetTagsEntity, UUID> {
}
