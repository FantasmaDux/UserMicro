package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.EmailUpdateSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailUpdateSessionRepository extends JpaRepository<EmailUpdateSessionEntity, UUID> {
    Optional<EmailUpdateSessionEntity> findByAccountId(UUID accountId);

    Optional<EmailUpdateSessionEntity> findByNewEmail(String email);

    void deleteByAccountId(UUID accountId);
}
