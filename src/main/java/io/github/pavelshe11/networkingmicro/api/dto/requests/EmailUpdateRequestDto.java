package io.github.pavelshe11.networkingmicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на облновление почты")
public class EmailUpdateRequestDto {
    @Schema(description = "Новая почта пользователя")
    private String email;
}
