package io.github.pavelshe11.networkingmicro.store.entities;

import io.github.pavelshe11.networkingmicro.store.enums.SpecializationLevelType;
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
@Table(name = "specialization")
public class SpecializationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

//    @Column(name = "count_of_courses", nullable = false)
//    private int countOfCourses;

//    @Column(name = "specialization_code", nullable = false)
    @Column(name = "specialization_code")
    private String specializationCode;

//    @Column(name = "specialization_level", nullable = false)
    @Column(name = "specialization_level")
    private SpecializationLevelType specializationLevel;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    private List<AccountEntity> accounts;

    @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InstitutionSpecialtiesEntity> institutionSpecialties;
}
