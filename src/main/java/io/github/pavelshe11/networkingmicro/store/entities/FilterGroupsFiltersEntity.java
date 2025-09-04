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
@Table(name = "filter_groups_filters")
public class FilterGroupsFiltersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_id", referencedColumnName = "id", nullable = false)
    private FilterEntity filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filter_group_id", referencedColumnName = "id", nullable = false)
    private FilterGroupEntity filterGroup;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
