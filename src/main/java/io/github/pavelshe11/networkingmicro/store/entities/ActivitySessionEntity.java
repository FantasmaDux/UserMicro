package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "activity_session")
public class ActivitySessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", unique = true)
    private AccountEntity account;

    @Column(name = "last_activity", nullable = false)
    @Builder.Default
    private Timestamp lastActivity = Timestamp.from(Instant.now());

    @Column(name = "inactivity_time_ms", nullable = false)
    @Builder.Default
//    private long inactivityTimeMs = 6L * 30 * 24 * 60 * 60 * 1000;
    private long inactivityTimeMs = 120000;
}
