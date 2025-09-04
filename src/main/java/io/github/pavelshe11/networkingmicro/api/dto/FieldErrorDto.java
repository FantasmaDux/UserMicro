package io.github.pavelshe11.networkingmicro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ ошибки")
public class FieldErrorDto {
    @Schema(description = "Поле ошибки")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String field;
    @Schema(description = "Сообщение ошибки для отображения рядом с полем ввода")
    private String message;
    @Schema(description = "ID объекта, к которому относится ошибка")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID objectId;

    public FieldErrorDto(String field, String message) {
        this.field = field;
        this.message = message;
    }
}