package io.github.fantasmadux.usermicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "institution_specialties")
public class InstitutionSpecialtiesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id", nullable = false)
    private EducationalInstitutionEntity educationalInstitution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", referencedColumnName = "id", nullable = false)
    private SpecializationEntity specialization;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
