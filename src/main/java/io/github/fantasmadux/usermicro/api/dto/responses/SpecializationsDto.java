package io.github.fantasmadux.usermicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Ответ на запрос получения списка специализаций")
public class SpecializationsDto {
    @NotNull
    @Schema(description = "ID специализации")
    private UUID id;

    @NotNull
    @Schema(description = "Код специализации")
    private String code;

    @NotNull
    @Schema(description = "Название специализации")
    private String name;
}
