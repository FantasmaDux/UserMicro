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
@Table(name = "match_with_specialist")
public class MatchWithSpecialistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account1_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account1;

    @ManyToOne
    @JoinColumn(name = "account2_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account2;

//    @ManyToOne
//    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
//    private Chat chat;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
