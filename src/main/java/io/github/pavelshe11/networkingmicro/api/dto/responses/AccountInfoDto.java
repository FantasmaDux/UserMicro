package io.github.pavelshe11.networkingmicro.api.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AccountInfoDto {
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String avatarUrl;
    private boolean professor;
    private boolean consulting;
    private boolean visible;
    private LocalDate dateOfBirth;
    private short courseNumber;

    private String cityName;
    private String specializationName;
    private String educationalInstitutionName;
}
