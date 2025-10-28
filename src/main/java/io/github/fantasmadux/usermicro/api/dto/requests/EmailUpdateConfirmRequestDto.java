package io.github.fantasmadux.usermicro.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос на подтверждение облновления почты")
public class EmailUpdateConfirmRequestDto {
    @Schema(description = "Новая почта пользователя")
    private String email;
    @Schema(description = "Код, который получил пользователь на новую почту")
    private String code;
}
