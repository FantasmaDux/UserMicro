package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findById(UUID id);
}
