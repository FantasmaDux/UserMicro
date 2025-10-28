package io.github.fantasmadux.usermicro.store.entities;

import io.github.fantasmadux.usermicro.store.enums.MediaType;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account", indexes = {
        @Index(name = "idx_account_last_name_id", columnList = "last_name, id")
})
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private CityEntity city;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id", nullable = false)
    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id")
    private EducationalInstitutionEntity educationalInstitution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", referencedColumnName = "id")
    private SpecializationEntity specialization;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "set_skills_id", referencedColumnName = "id", unique = true)
    SetSkillsEntity setSkills;

    @Lob // in DB it will be BLOB
    private byte[] avatar;

//    @Column(nullable = false)
//    private String nickname;

    @Column(name = "first_name", nullable = false)
    @Builder.Default
    private String firstName = "";

    @Column(name = "last_name", nullable = false)
    @Builder.Default
    private String lastName = "";

    @Column(name = "middle_name")
    @Builder.Default
    private String middleName = "";

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "main_email_contact", unique = true)
    private AccountContactInfoEntity mainEmailContact;

    @Column(name = "is_professor", nullable = false)
    @Builder.Default
    private boolean professor = false;

    @Column(name = "is_admin", nullable = false)
    @Builder.Default
    private boolean admin = false;

    @Column(name = "is_networking", nullable = false)
    @Builder.Default
    private boolean networking = false;

    @Column(name = "is_date_of_birth_visible", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VisibilityType dateOfBirthVisible = VisibilityType.PRIVATE;

    @Column(name = "is_city_visible", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VisibilityType cityVisible = VisibilityType.PRIVATE;

    @Column(name = "is_consulting", nullable = false)
    @Builder.Default
    private boolean consulting = false;

//    @Builder.Default
//    private float rating = 0.f;

    @Column(name = "date_of_birth")
    @Builder.Default
    private LocalDate dateOfBirth = null;

    @Column(name = "date_of_education_start")
    @Builder.Default
    private LocalDate dateOfEducationStart = null;

    @Column(name = "date_of_education_end")
    @Builder.Default
    private LocalDate dateOfEducationEnd = null;

    @Column(name = "is_accepted_privacy_policy")
    @Builder.Default
    private boolean acceptedPrivacyPolicy = false;

    @Column(name = "is_accepted_personal_data_processing")
    @Builder.Default
    private boolean acceptedPersonalDataProcessing = false;

    @Column(nullable = false)
    private String ip;

    @Column(name = "avatar_mimetype")
    @Enumerated(EnumType.STRING)
    private MediaType avatarMimeType;

    @Column(name = "bio")
    @Builder.Default
    private String bio = "";

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AccountContactInfoEntity> accountContactInfos;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InitiativeEntity> initiatives;

    @OneToMany(mappedBy = "account1", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MatchWithSpecialistEntity> matchesAsAccount1;

    @OneToMany(mappedBy = "account2", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MatchWithSpecialistEntity> matchesAsAccount2;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MatchWithInitiativeEntity> initiativeMatches;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ActivitySessionEntity activitySession;
}
