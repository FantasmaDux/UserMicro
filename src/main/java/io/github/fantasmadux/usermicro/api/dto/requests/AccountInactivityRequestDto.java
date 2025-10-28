package io.github.fantasmadux.usermicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на изменение времени бездействия аккаунта до удаления")
public class AccountInactivityRequestDto {
    @Schema(description = "Время бездействия аккаунта в мс. Стандартно 180 дней", examples = "15552000000")
    private long inactivityTimeMs;
}
