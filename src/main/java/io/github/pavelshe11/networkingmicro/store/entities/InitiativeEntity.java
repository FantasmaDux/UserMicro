package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "initiative")
public class InitiativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "set_tags_id", referencedColumnName = "id")
    private SetTagsEntity setTags;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private CityEntity city;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;

    // TODO: заглушка, пока нет мессенджера. Потом заменить
//    @OneToOne
//    @JoinColumn(name = "chats_folder_id", referencedColumnName = "id", nullable = false)
    @Column(name = "chats_folder_id", nullable = false)
    private UUID chatsFolderId;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @Lob
    private byte[] avatar;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "datetime_start")
    private ZonedDateTime datetimeStart;

    @Column(name = "datetime_end")
    private ZonedDateTime datetimeEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InitiativeEntity.InitiativeStatusType type;

    public enum InitiativeStatusType {
        active,
        inactive
    }

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleInInitiativeEntity> roles;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchWithInitiativeEntity> matches;
}
