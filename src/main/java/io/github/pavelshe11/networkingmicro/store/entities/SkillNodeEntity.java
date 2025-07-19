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
@Table(name = "skill_node")
public class SkillNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "skill_node_parent_id", referencedColumnName = "id")
    private SkillNodeEntity skillNodeParent;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_ok", nullable = false)
    private String ok;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
