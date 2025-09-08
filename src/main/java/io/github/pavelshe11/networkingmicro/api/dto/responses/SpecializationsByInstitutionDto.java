package io.github.pavelshe11.networkingmicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Ответ на запрос получения списка специализаций по ВУЗу")
public class SpecializationsByInstitutionDto {
    @NotNull
    @Schema(description = "Список специализаций в ВУЗе")
    @Valid
    private List<SpecializationsDto> specializationsByInstitution;

}
