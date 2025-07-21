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
@Table(name = "portfolio_item")
public class PortfolioItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "description_skill_area_id", referencedColumnName = "id", nullable = false)
    private DescriptionSkillAreaEntity descriptionSkillArea;

    @Column(nullable = false)
    private String text;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
