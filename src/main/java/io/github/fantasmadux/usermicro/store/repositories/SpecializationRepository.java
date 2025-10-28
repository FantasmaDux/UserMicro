package io.github.fantasmadux.usermicro.store.repositories;

import io.github.fantasmadux.usermicro.store.entities.SpecializationEntity;
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
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findAllSpecializationsWithKeysetPaginationAfter(
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND s.clean_code LIKE :keyword
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithCodeAfter(
            @Param("institutionId") UUID institutionId,
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE s.clean_code LIKE :keyword
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByCodeWithoutInstitutionAfter(
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND LOWER(s.name) LIKE LOWER(:keyword)
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithNameAfter(
            @Param("institutionId") UUID institutionId,
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE LOWER(s.name) LIKE LOWER(:keyword)
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByNameWithoutInstitutionAfter(
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND s.clean_code = :codePattern
      AND LOWER(s.name) LIKE LOWER(:namePattern)
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithCodeAndNameAfter(
            @Param("institutionId") UUID institutionId,
            @Param("codePattern") String codePattern,
            @Param("namePattern") String namePattern,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE
      s.clean_code = :codePattern
      AND LOWER(s.name) LIKE LOWER(:namePattern)
      AND (:cursorName IS NULL
           OR s.name > :cursorName
           OR (s.name = :cursorName AND s.id > :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByCodeAndNameWithoutInstitutionAfter(
            @Param("codePattern") String codePattern,
            @Param("namePattern") String namePattern,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );


    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findAllSpecializationsWithKeysetPaginationBefore(
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND s.clean_code = :codePattern
      AND LOWER(s.name) LIKE LOWER(:namePattern)
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithCodeAndNameBefore
    (
            @Param("institutionId") UUID institutionId,
            @Param("codePattern") String codePattern,
            @Param("namePattern") String namePattern,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE
      s.clean_code = :codePattern
      AND LOWER(s.name) LIKE LOWER(:namePattern)
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByCodeAndNameWithoutInstitutionBefore(
            @Param("codePattern") String codePattern,
            @Param("namePattern") String namePattern,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );




    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND s.clean_code LIKE :keyword
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name, s.id
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithCodeBefore(
            @Param("institutionId") UUID institutionId,
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE s.clean_code LIKE :keyword
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByCodeWithoutInstitutionBefore(
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    LEFT JOIN institution_specialties intspec ON intspec.specialization_id = s.id
    LEFT JOIN educational_institution ei ON ei.id = intspec.educational_institution_id
    WHERE ei.id = :institutionId
      AND LOWER(s.name) LIKE LOWER(:keyword)
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByInstitutionWithNameBefore(
            @Param("institutionId") UUID institutionId,
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

    @Query(value = """
    SELECT s.id,
           s.clean_code AS code,
           s.name
    FROM specialization s
    WHERE LOWER(s.name) LIKE LOWER(:keyword)
      AND (:cursorName IS NULL
           OR s.name < :cursorName
           OR (s.name = :cursorName AND s.id < :cursorId))
    ORDER BY s.name DESC, s.id DESC
    LIMIT :size
    """, nativeQuery = true)
    List<Object[]> findByNameWithoutInstitutionBefore(
            @Param("keyword") String keyword,
            @Param("cursorName") String cursorName,
            @Param("cursorId") UUID cursorId,
            @Param("size") int size
    );

}
