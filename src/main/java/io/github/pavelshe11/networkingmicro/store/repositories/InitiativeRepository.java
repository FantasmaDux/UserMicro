package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.InitiativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InitiativeRepository extends JpaRepository<InitiativeEntity, UUID> {
    @Modifying
    @Query("UPDATE InitiativeEntity i SET i.account.id = null WHERE i.account.id = :accountId")
    void updateAccountToNull(@Param("accountId") UUID accountId);
}
