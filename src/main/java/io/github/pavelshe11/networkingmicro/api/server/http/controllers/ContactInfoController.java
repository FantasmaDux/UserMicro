package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.annotations.CommonApiResponses;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoDeleteRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.services.AccountContactInfoService;
import io.github.pavelshe11.networkingmicro.util.JwtUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @CommonApiResponses
    @PostMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> updateAccountContactInfo(
            @Parameter(description = "Список контактов на вставку/обновление")
            @RequestBody ContactInfoUpdateRequestDto request
            ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountContactInfoService.updateContactInfo(accountId, request);
        return ResponseEntity.ok().build();
    }

    @CommonApiResponses
    @DeleteMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> deleteAccountContactInfo(
            @Parameter(description = "Список контактов на удаление")
            @RequestBody ContactInfoDeleteRequestDto request
    ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountContactInfoService.deleteContactInfoMethod(accountId, request);
        return ResponseEntity.ok().build();
    }
}
