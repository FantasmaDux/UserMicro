package io.github.pavelshe11.networkingmicro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ответ ошибки валидации полей")
public class ErrorDto {
    @Schema(description = "Наименование ошибки")
    private String error;
    @Schema(description = "Список ошибок")
    private List<FieldErrorDto> detailedErrors;
}
