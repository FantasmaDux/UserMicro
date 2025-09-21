package io.github.pavelshe11.networkingmicro.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ на запрос получения списка аккаунтов")
public class AccountShortDto {
    @NotNull
    @Schema(description = "ID аккаунта")
    private UUID id;

    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @Schema(description = "Имя пользователя")
    private String firstName;

    @Schema(description = "Отчество пользователя")
    private String middleName;

    @Schema(description = "Почта пользователя")
    private String email;
}
