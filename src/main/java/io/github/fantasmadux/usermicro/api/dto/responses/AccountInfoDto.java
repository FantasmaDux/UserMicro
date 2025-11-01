package io.github.fantasmadux.usermicro.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.fantasmadux.usermicro.store.enums.VisibilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ с информацией по аккаунту пользователя")
public class AccountInfoDto {
    @Schema(description = "Имя пользователя")
    private String firstName;
    @Schema(description = "Фамилия пользователя")
    private String lastName;
    @Schema(description = "Отчество пользователя")
    private String middleName;
    @Schema(description = "Информация о себе")
    private String bio;
    @Schema(description = "Почта пользователя")
    private String email;
    @Schema(description = "Является ли пользователь преподавателем")
    private boolean professor;
    @Schema(description = "Является ли пользователь консультатном")
    private boolean consulting;
    @Schema(description = "Видим ли пользователь")
    private boolean networking;
    @Schema(description = "Кому виден день рождения пользователя")
    private VisibilityType dateOfBirthVisible;
    @Schema(description = "Кому виден город пользователя")
    private VisibilityType cityVisible;
    @Schema(description = "День рождения пользователя", example = "2004-07-28")
    private LocalDate dateOfBirth;
    @Schema(description = "День начала обучения пользователя", example = "2021-09-01")
    private LocalDate dateOfEducationStart;
    @Schema(description = "День конца обучения пользователя", example = "2025-09-01")
    private LocalDate dateOfEducationEnd;
    @Schema(description = "Время бездействия аккаунта в мс. Стандартно 180 дней", examples = "15552000000")
    private long inactivityTimeMs;

    @Schema(description = "Название города пользователя")
    private String cityName;
    @Schema(description = "Название специализации пользователя")
    private String specializationName;
    @Schema(description = "ID специализации")
    private UUID specializationId;
}
