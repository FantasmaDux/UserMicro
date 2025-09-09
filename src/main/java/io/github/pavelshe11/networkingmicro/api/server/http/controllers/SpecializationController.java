package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.annotations.CommonApiResponses;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.SpecializationsDto;
import io.github.pavelshe11.networkingmicro.services.SpecializationService;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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


    @Operation(summary = "Метод получения специализаций по вузу и ключевым словам")
    @CommonApiResponses
    @ApiResponse(
            responseCode = "200",
            description = "Список специализаций получен"
    )
    @GetMapping(value = "/specializations", produces = "application/json")
    public Slice<SpecializationsDto> getSpecializations(
            @Parameter(description = "ID ВУЗа для поиска")
            @RequestParam(required = false) UUID institutionId,
            @Parameter(description = "Код специальности или название",
            example = "08.03 или информатика")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Курсор для постраничного вывода (Содержит Base64 из name и id специальности " +
                    "последнего элемента прошлого ответа).",
            example = "0JDQvdC40YbQsNC80L7QstCwLGZjZDJjMGFmLWY5YzYtNGNhZi05MWQyLWE1N2M2ZjdkMmI2NQ==")
            @RequestParam(required = false) String cursor,
            @Parameter(description = "Размер вывода. По дефолту 10")
            @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        return specializationService.getSpecializations(institutionId, keyword, cursor, pageSize);
    }
}
