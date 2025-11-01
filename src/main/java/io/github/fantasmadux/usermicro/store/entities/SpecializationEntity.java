package io.github.fantasmadux.usermicro.store.entities;

import io.github.fantasmadux.usermicro.store.enums.SpecializationLevelType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "specialization", indexes = {
        @Index(name = "idx_specialization_name_id", columnList = "name, id"),
        @Index(name = "idx_specialization_code_clean", columnList = "clean_code")
})
public class SpecializationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

//    @Column(name = "specialization_code", nullable = false)
    @Column(name = "specialization_code")
    private String specializationCode;

    @Column(name = "clean_code")
    private String cleanCode;

//    @Column(name = "specialization_level", nullable = false)
    @Column(name = "specialization_level")
    private SpecializationLevelType specializationLevel;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    private List<AccountEntity> accounts;

}
