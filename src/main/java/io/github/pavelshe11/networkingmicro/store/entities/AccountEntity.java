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

    @ManyToOne
    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id", nullable = false)
//    @JoinColumn(name = "educational_institution_id", referencedColumnName = "id")
    private EducationalInstitutionEntity educationalInstitution;

    @ManyToOne
    @JoinColumn(name = "specialization_id", referencedColumnName = "id")
    private SpecializationEntity specialization;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "set_skills_id", referencedColumnName = "id", unique = true)
    @JoinColumn(name = "set_skills_id", referencedColumnName = "id")
    SetSkillsEntity setSkills;

    @Lob // in DB it will be BLOB
    private byte[] avatar;

//    @Column(unique = true, nullable = false)
    @Column(nullable = false)
    private String nickname;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_professor", nullable = false)
    private boolean professor;

    @Column(name = "is_admin", nullable = false)
    private boolean admin;

    @Column(name = "is_visible", nullable = false)
    private boolean visible;

    @Column(name = "is_consulting", nullable = false)
    private boolean consulting;

    @Builder.Default
    private float rating = 0.f;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "course_number")
    private short courseNumber;

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
