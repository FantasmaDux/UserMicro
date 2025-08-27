package io.github.pavelshe11.networkingmicro.store.entities;

import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
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
@Table(name = "account_contact_info")
public class AccountContactInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false, unique = true)
    private String contact;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "contact_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactMethodType contactMethod;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
