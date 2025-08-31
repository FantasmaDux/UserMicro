package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.annotations.CommonApiResponses;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoDeleteRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoAddRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateListRequestDto;
import io.github.pavelshe11.networkingmicro.services.AccountContactInfoService;
import io.github.pavelshe11.networkingmicro.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "Управление контактной информацией", description = "API  для работы с контактной информацией пользователя")
@SecurityRequirement(name = "bearerTokenAuth")
@RequestMapping("/networking/v1/accountContactInfo")
public class ContactInfoController {

    private final JwtUtil jwtUtil;
    private final AccountContactInfoService accountContactInfoService;

    @Operation(summary = "Метод вставки контакта пользователя")
    @CommonApiResponses
    @ApiResponse(
            responseCode = "200",
            description = "Контакт добавлен"
            )
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> updateAccountContactInfo(
            @Parameter(description = "Контакт для добавления")
            @Valid @RequestBody ContactInfoAddRequestDto request
            ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountContactInfoService.addContactInfo(accountId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод удаления контактов пользователя")
    @CommonApiResponses
    @ApiResponse(
            responseCode = "200",
            description = "Контакты удалены"
    )
    @DeleteMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> deleteAccountContactInfo(
            @Parameter(description = "Список контактов на удаление")
            @RequestBody ContactInfoDeleteRequestDto request
    ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountContactInfoService.deleteContactInfoMethod(accountId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Редактирование контакта по ID")
    @CommonApiResponses
    @ApiResponse(responseCode = "200", description = "Контакт обновлён")
    @PatchMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> editContactInfoById(
            @Valid @RequestBody ContactInfoUpdateListRequestDto request
    ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountContactInfoService.editContactInfoById(accountId, request);
        return ResponseEntity.ok().build();
    }
}
