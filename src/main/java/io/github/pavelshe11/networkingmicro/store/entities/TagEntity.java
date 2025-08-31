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
@Table(name = "tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TagFromSetTagsEntity> tagFromSetTags;
}
