package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "filter")
public class FilterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String info;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FilterType type;

    public enum FilterType {
        MultipleOptions,
        OneOfOptions,
        Range,
        SelectorSkills,
        SelectorDate
    }

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "filter", fetch = FetchType.LAZY)
    private Set<FilterGroupsFiltersEntity> filterGroupsFilters;
}
