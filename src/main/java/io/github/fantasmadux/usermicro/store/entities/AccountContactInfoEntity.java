package io.github.fantasmadux.usermicro.store.entities;

import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false, unique = true)
    private String contact;

    @Column(name = "icon_url")
    private String iconUrl;

    @Builder.Default
    @Column(name = "visibility", nullable = false)
    private VisibilityType visibility = VisibilityType.PRIVATE;


    @Column(name = "contact_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContactMethodType contactMethod;

    @Column(name = "modifiable", nullable = false)
    private boolean modifiable = true;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
