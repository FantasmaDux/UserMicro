package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.ActivitySessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivitySessionRepository extends JpaRepository<ActivitySessionEntity, UUID> {
    Optional<ActivitySessionEntity> findByAccount(AccountEntity account);

    Optional<ActivitySessionEntity> findByAccountId(UUID accountId);
}
