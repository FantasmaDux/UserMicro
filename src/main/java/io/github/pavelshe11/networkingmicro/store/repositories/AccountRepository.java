package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    Optional<AccountEntity> findByMainEmailContactContact(String contact);
    boolean existsByMainEmailContactContact(String contact);

    @Query(value =
    """
                SELECT DISTINCT account.educational_institution_id, account.specialization_id 
                    FROM account
                    WHERE account.specialization_id IS NOT NULL
                    AND account.educational_institution_id IS NOT NULL
            """, nativeQuery = true)
    List<Object[]> findActiveInstitutionSpecializations();
}
