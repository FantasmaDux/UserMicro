package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.entities.ActivitySessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivitySessionRepository extends JpaRepository<ActivitySessionEntity, UUID> {
    Optional<ActivitySessionEntity> findByAccount(AccountEntity account);

    void deleteByAccount(AccountEntity account);
}
