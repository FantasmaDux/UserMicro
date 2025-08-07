package io.github.pavelshe11.networkingmicro.store.entities;

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
@Table(name = "educational_institution")
public class EducationalInstitutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "domen_name", nullable = false)
    private String domenName;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
//    @OneToMany(mappedBy = "educationalInstitution")
//    private List<AccountEntity> accounts;

    @OneToMany(mappedBy = "educationalInstitution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InstitutionSpecialtiesEntity> institutionSpecialties;
}
