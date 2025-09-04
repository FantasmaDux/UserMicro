package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "skill_node")
public class SkillNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_node_parent_id", referencedColumnName = "id")
    private SkillNodeEntity skillNodeParent;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_ok", nullable = false)
    private boolean ok;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "skillNodeParent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SkillNodeEntity> skillNodeChildren;

    // For two-way communication with FK
    @OneToMany(mappedBy = "skillNode", fetch = FetchType.LAZY)
    private List<SkillSetSkillsEntity> skillSetSkills;
}
