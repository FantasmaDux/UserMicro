package io.github.pavelshe11.networkingmicro.store.entities;

import io.github.pavelshe11.networkingmicro.store.enums.ChoiceType;
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
@Table(name = "record_initiative_view")
public class RecordInitiativeViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "initiative_id", referencedColumnName = "id", nullable = false)
    private InitiativeEntity initiative;

    @Column(name = "datetime_check", nullable = false)
    private ZonedDateTime datetimeCheck;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChoiceType choice;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

}
