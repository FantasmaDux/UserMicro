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

    @Query(value = """
    SELECT ac.id,
           ac.last_name,
           ac.first_name,
           ac.middle_name,
           ac_contact.contact
    FROM account ac
    LEFT JOIN account_contact_info ac_contact ON ac.main_email_contact = ac_contact.id
     WHERE
            (:lastName IS NULL OR LOWER(ac.last_name) LIKE CONCAT(:lastName, '%'))
            AND (:firstName IS NULL OR LOWER(ac.first_name) LIKE CONCAT(:firstName, '%'))
            AND (:middleName IS NULL OR LOWER(ac.middle_name) LIKE CONCAT(:middleName, '%'))
            AND (
                :cursorLastName IS NULL
                OR (ac.last_name > :cursorLastName)
                OR (ac.last_name = :cursorLastName AND ac.id > :cursorId)
            )
    ORDER BY ac.last_name, ac.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findAllByFullNameWithKeysetPaginationAfter(String lastName, String firstName, String middleName, String cursorLastName, UUID cursorId, int size);

    @Query(value = """
    SELECT ac.id,
           ac.last_name,
           ac.first_name,
           ac.middle_name,
           ac_contact.contact
    FROM account ac
    LEFT JOIN account_contact_info ac_contact ON ac.main_email_contact = ac_contact.id
     WHERE
            (:lastName IS NULL OR LOWER(ac.last_name) LIKE CONCAT(:lastName, '%'))
            AND (:firstName IS NULL OR LOWER(ac.first_name) LIKE CONCAT(:firstName, '%'))
            AND (:middleName IS NULL OR LOWER(ac.middle_name) LIKE CONCAT(:middleName, '%'))
            AND (
                :cursorLastName IS NULL
                OR (ac.last_name < :cursorLastName)
                OR (ac.last_name = :cursorLastName AND ac.id < :cursorId)
            )
    ORDER BY ac.last_name DESC, ac.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findAllByFullNameWithKeysetPaginationBefore(String lastName, String firstName, String middleName, String cursorLastName, UUID cursorId, int size);
}
