package io.github.pavelshe11.networkingmicro.store.entities;

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
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private CityEntity city;

//    @ManyToOne
//    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id", nullable = false)
//    private EducationalInstitutionEntity educationalInstitution;

    @ManyToOne
    @JoinColumn(name = "specialization_id", referencedColumnName = "id")
    private SpecializationEntity specialization;

//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "set_skills_id", referencedColumnName = "id", unique = true)
//    SetSkillsEntity setSkills;

//    @Lob // in DB it will be BLOB
//    private byte[] avatar;

//    @Column(nullable = false)
//    private String nickname;

    @Column(name = "first_name", nullable = false)
    @Builder.Default
    private String firstName = "";

    @Column(name = "last_name")
    @Builder.Default
    private String lastName = "";

    @Column(name = "middle_name")
    @Builder.Default
    private String middleName = "";

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_professor", nullable = false)
    @Builder.Default
    private boolean professor = false;

    @Column(name = "is_admin", nullable = false)
    @Builder.Default
    private boolean admin = false;

    @Column(name = "is_visible", nullable = false)
    @Builder.Default
    private boolean visible = false;

    @Column(name = "is_consulting", nullable = false)
    @Builder.Default
    private boolean consulting = false;

    @Builder.Default
    private float rating = 0.f;

    @Column(name = "date_of_birth")
    @Builder.Default
    private LocalDate dateOfBirth = LocalDate.now();

    @Column(name = "course_number")
    @Builder.Default
    private short courseNumber = 0;

    @Column(name = "is_accepted_privacy_policy")
    @Builder.Default
    private boolean acceptedPrivacyPolicy = false;

    @Column(name = "is_accepted_personal_data_processing")
    @Builder.Default
    private boolean acceptedPersonalDataProcessing = false;

    @Column(nullable = false)
    private String ip;

    @Builder.Default
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // For two-way communication with FK
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountContactInfoEntity> accountContactInfos;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InitiativeEntity> initiatives;

    @OneToMany(mappedBy = "account1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchWithSpecialistEntity> matchesAsAccount1;

    @OneToMany(mappedBy = "account2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchWithSpecialistEntity> matchesAsAccount2;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchWithInitiativeEntity> initiativeMatches;


}
