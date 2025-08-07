package io.github.pavelshe11.networkingmicro.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AccountCreationResponse {

    private UUID id;

    private UUID cityId;

//    private UUID educationalInstitutionId;

    private UUID specializationId;

//    private UUID setSkillsId;

//    @Lob
//    private byte[] avatar;

//    private String nickname;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    private boolean professor;

    private boolean admin;

    private boolean visible;

    private boolean consulting;

    private float rating;

    private LocalDate dateOfBirth;

    private short courseNumber;

    private boolean acceptedPrivacyPolicy;

    private boolean acceptedPersonalDataProcessing;

    private String ip;

    private Instant createdAt;
}
