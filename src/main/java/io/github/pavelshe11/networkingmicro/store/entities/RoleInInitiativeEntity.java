package io.github.pavelshe11.networkingmicro.store.entities;

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
@Table(name = "role_in_initiative")
public class RoleInInitiativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "set_skills_id", referencedColumnName = "id")
    private SetSkillsEntity setSkills;

    @ManyToOne
    @JoinColumn(name = "initiative_id", referencedColumnName = "id", nullable = false)
    private InitiativeEntity initiative;

    @Column (nullable = false)
    private String role;

    private String description;

    private int schedule;

    @Column(name = "years_experience")
    private float yearsExperience;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_format")
    private WorkFormatType workFormat;
    public enum WorkFormatType {
        faceToFace,
        remotely
    }

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
