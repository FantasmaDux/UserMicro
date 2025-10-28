package io.github.fantasmadux.usermicro.store.entities;

import io.github.fantasmadux.usermicro.store.enums.ChoiceType;
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
@Table(name = "record_profile_view")
public class RecordProfileViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_viewer_id", referencedColumnName = "id", nullable = false)
    private AccountEntity accountViewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_owner_id", referencedColumnName = "id", nullable = false)
    private AccountEntity accountOwner;

    @Column(name = "datetime_check", nullable = false)
    private ZonedDateTime datetimeCheck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChoiceType choice;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

}
