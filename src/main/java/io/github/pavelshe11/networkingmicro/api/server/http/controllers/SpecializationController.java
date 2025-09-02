package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.annotations.CommonApiResponses;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.pavelshe11.networkingmicro.services.SpecializationService;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "Управление специализациями", description = "API для работы со специализациями университетов")
@SecurityRequirement(name = "bearerTokenAuth")
@RequestMapping("/networking/v1/specialization")
public class SpecializationController {

    private final SpecializationService specializationService;

    @Operation(summary = "Метод получения специализаций университета")
    @CommonApiResponses
    @ApiResponse(
            responseCode = "200",
            description = "Список специализаций получен"
    )
    @GetMapping(value = "/byInstitution", produces = "application/json")
    public SpecializationsByInstitutionDto getSpecializations(
            @RequestParam UUID institutionId
    ) {
        return specializationService.getSpecializationsByInstitution(institutionId);
    }
}
