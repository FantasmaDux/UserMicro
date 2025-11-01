package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.AccountContactInfoEntity;
import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
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
