package io.github.fantasmadux.usermicro.store.entities;

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
@Table(name = "description_skill_area")
public class DescriptionSkillAreaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_set_skills_id", referencedColumnName = "id", unique = true, nullable = false)
    private SkillSetSkillsEntity skillSetSkills;

    @Column(name = "years_experience")
    private float yearsExperience;

    @Column(name = "activity_description", nullable = false)
    private String activityDescription;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "descriptionSkillArea", fetch = FetchType.LAZY)
    private List<PortfolioItemEntity> portfolioItems;

}
