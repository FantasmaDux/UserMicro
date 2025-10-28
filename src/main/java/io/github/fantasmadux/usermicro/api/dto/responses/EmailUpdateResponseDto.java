package io.github.fantasmadux.usermicro.api.dto.responses;

import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Ответ на запрос обновления почты пользователя")
public class EmailUpdateResponseDto {
    @Schema(description = "Паттерн генерации кода")
    private String codePattern;
    @Schema(description = "Время действия кода")
    private long codeExpires;
}
