package io.github.fantasmadux.usermicro.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
