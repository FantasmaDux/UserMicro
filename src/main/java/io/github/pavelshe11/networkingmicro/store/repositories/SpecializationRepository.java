package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecializationRepository extends JpaRepository<SpecializationEntity, UUID> {
    boolean existsByName(String specializationName);

    Optional<SpecializationEntity> findByName(String specializationName);

    @Query(value = """
            SELECT 
                s.id,
                substring(s.specialization_code FROM '^[0-9]+\\\\.(.+)$') AS code,
                s.name
            FROM specialization s
            LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id 
            LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
            WHERE (:institutionId IS NULL OR ei.id = :institutionId)
              AND (:keyword IS NULL 
                    OR s.name ILIKE CONCAT('%', :keyword, '%')
                    OR s.specialization_code ILIKE CONCAT('%', :keyword, '%'))
              AND (
                          (
                            :cursorName IS NULL
                            OR (s.name > :cursorName)
                            OR (s.name = :cursorName AND s.id > :cursorId)
                          )
              )
            ORDER BY s.name, s.id
            LIMIT :size
            """, nativeQuery = true)
    List<Object[]> findSpecializationsWithKeysetPagination(
            @Param("institutionId") UUID institutionId,
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );
}
