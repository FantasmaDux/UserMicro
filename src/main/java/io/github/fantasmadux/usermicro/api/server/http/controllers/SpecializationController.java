package io.github.fantasmadux.usermicro.api.server.http.controllers;

import io.github.fantasmadux.usermicro.annotations.CommonApiResponses;
import io.github.fantasmadux.usermicro.api.dto.responses.SpecializationPageDto;
import io.github.fantasmadux.usermicro.api.dto.responses.SpecializationsByInstitutionDto;
import io.github.fantasmadux.usermicro.services.SpecializationService;
import io.github.fantasmadux.usermicro.store.enums.CursorDestinationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "Управление специализациями", description = "API для работы со специализациями университетов")
@SecurityRequirement(name = "bearerTokenAuth")
@RequestMapping("/user/v1/specialization")
public class SpecializationController {

    private final SpecializationService specializationService;

    @Operation(summary = "Метод получения специализаций по вузу и ключевым словам")
    @CommonApiResponses
    @ApiResponse(
            responseCode = "200",
            description = "Список специализаций получен"
    )
    @GetMapping(value = "/specializations", produces = "application/json")
    public SpecializationPageDto getSpecializations(
            @Parameter(description = "ID ВУЗа для поиска")
            @RequestParam(required = false) UUID institutionId,
            @Parameter(description = "Код специальности или название",
            example = "08.03 информатика")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Курсор для постраничного вывода (Содержит Base64 из name и id специальности " +
                    "последнего элемента прошлого ответа).",
            example = "0JDQvdC40YbQsNC80L7QstCwLGZjZDJjMGFmLWY5YzYtNGNhZi05MWQyLWE1N2M2ZjdkMmI2NQ==")
            @RequestParam(required = false) String cursor,
            @Parameter(description = "Выбор элементов до указанного курсора или после (before/after)")
            @RequestParam(required = false) CursorDestinationType cursorDestination,
            @Parameter(description = "Размер вывода. По дефолту 10")
            @RequestParam(name = "size", defaultValue = "10") int pageSize) {
        return specializationService.getSpecializations(institutionId, keyword, cursor, pageSize, cursorDestination);
    }
}
