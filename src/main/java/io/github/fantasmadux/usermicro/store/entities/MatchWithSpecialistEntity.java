package io.github.fantasmadux.usermicro.store.entities;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account1_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account2_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account2;

//    @OneToOne
//    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
//    private Chat chat;

    // TODO: заглушка для чата. Потом заменить
    @Column(name = "chat_id")
    private UUID chatId;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
