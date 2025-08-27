package io.github.pavelshe11.networkingmicro.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.pavelshe11.networkingmicro.api.dto.AccountContactInfoDto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
    @Schema(description = "Почта пользователя")
    private String email;
    @Schema(description = "Является ли пользователь преподавателем")
    private boolean professor;
    @Schema(description = "Является ли пользователь консультатном")
    private boolean consulting;
    @Schema(description = "Видим ли пользователь")
    private boolean visible;
    @Schema(description = "День рождения пользователя", example = "1039899600")
    private LocalDate dateOfBirth;
    @Schema(description = "Курс пользователя")
    private Short courseNumber;

    @Schema(description = "Название города пользователя")
    private String cityName;
    @Schema(description = "Название специализации пользователя")
    private String specializationName;
    @Schema(description = "Название домена пользователя")
    private String educationalInstitutionName;
    @Schema(description = "Список контактов пользователя")
    private List<AccountContactInfoDto> contactMethods;
}
