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
@Table(name = "skill_set_skills")
public class SkillSetSkillsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_node_id", referencedColumnName = "id")
    private SkillNodeEntity skillNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_skills_id", referencedColumnName = "id")
    private SetSkillsEntity setSkills;


    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToOne(mappedBy = "skillSetSkills")
    private DescriptionSkillAreaEntity descriptionSkillArea;
}
