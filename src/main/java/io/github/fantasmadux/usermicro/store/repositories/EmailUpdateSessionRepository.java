package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.EmailUpdateSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailUpdateSessionRepository extends JpaRepository<EmailUpdateSessionEntity, UUID> {
    Optional<EmailUpdateSessionEntity> findByAccountId(UUID accountId);

    Optional<EmailUpdateSessionEntity> findByNewEmail(String email);

    void deleteByAccountId(UUID accountId);
}
