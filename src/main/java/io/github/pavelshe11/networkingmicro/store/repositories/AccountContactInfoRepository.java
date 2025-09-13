package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountContactInfoRepository extends JpaRepository<AccountContactInfoEntity, UUID> {
    boolean existsByContactIgnoreCase(String normalizedContact);

    Optional<AccountContactInfoEntity> findByContact(String email);

    boolean existsByContactAndAccount(String trimmedContact, AccountEntity account);

    Optional<AccountContactInfoEntity> findByContactAndAccount(String normalizedContact, AccountEntity account);

    void deleteByAccount(AccountEntity account);
}
