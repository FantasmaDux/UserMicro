package io.github.pavelshe11.networkingmicro.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_update_session")
public class EmailUpdateSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "new_email", nullable = false)
    private String newEmail;

    @Column(nullable = false)
    private String code;

    @Column(name = "code_expires", nullable = false)
    private Timestamp codeExpires;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());
}
