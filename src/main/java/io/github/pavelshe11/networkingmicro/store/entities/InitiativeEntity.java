package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.ZonedDateTime;
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

    @ManyToOne
    @JoinColumn(name = "set_tags_id", referencedColumnName = "id")
    private SetTagsEntity setTags;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
    private CityEntity city;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;

    // TODO: это как заткнуть, если оно из мессенджера
//    @Column(name = "chats_folder", nullable = false)
//    private ChatsFolder chatsFolder;

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


}
