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
@Table(name = "tag_from_set_tags")
public class TagFromSetTagsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "set_tags_id", referencedColumnName = "id", nullable = false)
    private SetTagsEntity setTags;

    @ManyToOne
    @JoinColumn(name = "tag_id", referencedColumnName = "id", nullable = false)
    private TagEntity tag;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
