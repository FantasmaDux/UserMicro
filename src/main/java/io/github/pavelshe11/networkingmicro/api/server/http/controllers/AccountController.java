package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.api.dto.requests.AvatarUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.AccountInfoDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.EmailUpdateResponseDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.services.AccountInfoService;
import io.github.pavelshe11.networkingmicro.services.AccountUpdateService;
import io.github.pavelshe11.networkingmicro.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Tag(name = "Account", description = "API аккаунта, networking")
@SecurityRequirement(name = "bearerTokenAuth")
@RequestMapping("/networking/v1/account")
public class AccountController {
    private final AccountUpdateService accountUpdateService;
    private final AccountInfoService accountInfoService;
    private final JwtUtil jwtUtil;

    // Аналог метода с jwt извлечением id
    @Operation(summary = "Метод обновления данных аккаунта пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Запрос не прошёл аутентификацию"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "400", description = "Неверно указаны данные")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Список обновляемых полей аккаунта",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object"),
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                            value = """
                {
                  "firstName": "test",
                  "lastName": "test",
                  "middleName": "test",
                  "professor": true,
                  "consulting": true,
                  "visible": true,
                  "dateOfBirth": 1039899600,
                  "city": "44c56ff4-7db4-411a-8d0f-c2f326ca3666",
                  "specialization": " 8fb2c6e7-b0f7-48ef-89f5-3f33cc7b626e",
                  "courseNumber": 3
                }
                """
                    )
            )
    )
    @PatchMapping("")
    public ResponseEntity<Void> updateAccountData(
            @Parameter(description = "Обновляемые поля аккаунта в формате JSON")
            @RequestBody Map<String, Object> updatedData) {
        UUID accountId = jwtUtil.claimAccountId();
        accountUpdateService.updateAccount(accountId, updatedData);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод обновления почты пользователя по id")
    @PatchMapping(value = "/email")
    public EmailUpdateResponseDto updateEmail(
            @RequestBody EmailUpdateRequestDto request) {
        UUID accountId = jwtUtil.claimAccountId();

        return accountUpdateService.updateEmail(request, accountId);
    }

    @Operation(summary = "Метод подтверждения обновления почты пользователя по id")
    @PatchMapping("/confirmEmail")
    public ResponseEntity<Void> updateEmailConfirm(
            @RequestBody EmailUpdateConfirmRequestDto request) {
        UUID accountId = jwtUtil.claimAccountId();

        accountUpdateService.confirmEmail(request, accountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод добавления/обновления аватара пользователя по id")
    @PatchMapping(path = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAvatar(
            @ModelAttribute AvatarUpdateRequestDto request) {
        try {
            byte[] avatarBytes = request.getAvatar().getBytes();
            UUID accountId = jwtUtil.claimAccountId();

            accountUpdateService.updateAvatar(accountId, avatarBytes);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ServerAnswerException();
        }
    }

    @Operation(summary = "Метод удаления аккаунта пользователя по id")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteAccount() {
        UUID accountId = jwtUtil.claimAccountId();
        accountUpdateService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод получения информации аккаунта пользователя по id")
    @GetMapping("")
    public AccountInfoDto getAccount() {
        UUID accountId = jwtUtil.claimAccountId();
        return accountInfoService.getAccountFullInfo(accountId);
    }

    @Operation(summary = "Метод получения аватара пользователя по id")
    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar() {
        UUID accountId = jwtUtil.claimAccountId();
        byte[] avatar = accountInfoService.getAvatar(accountId);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(avatar);
    }

//     Передается мапа, так как при put с dto надо все поля задавать. С мапой можно передать на
//     обновление только часть полей
//    @PatchMapping("/{accountId}")
//    public ResponseEntity<Void> updateAccountData(
//            @PathVariable UUID accountId,
//            @RequestBody Map<String, Object> updatedData) {
//        accountUpdateService.updateAccount(accountId, updatedData);
//        return ResponseEntity.ok().build();
//    }
//    @PatchMapping(value = "/{accountId}/email")
//    public EmailUpdateResponseDto updateEmail(
//            @PathVariable UUID accountId,
//            @RequestBody EmailUpdateRequestDto request) {
//
//        return accountUpdateService.updateEmail(request, accountId);
//    }
//    @PatchMapping("/{accountId}/confirmEmail")
//    public ResponseEntity<Void> updateEmailConfirm(
//            @PathVariable UUID accountId,
//            @RequestBody EmailUpdateConfirmRequestDto request) {
//        accountUpdateService.confirmEmail(request, accountId);
//        return ResponseEntity.ok().build();
//    }
//    @PatchMapping(path = "/{accountId}/avatar",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Void> updateAvatar(
//            @PathVariable UUID accountId,
//            @ModelAttribute AvatarUpdateRequestDto request) {
//        try {
//            byte[] avatarBytes = request.getAvatar().getBytes();
//            accountUpdateService.updateAvatar(accountId, avatarBytes);
//            return ResponseEntity.ok().build();
//        } catch (IOException e) {
//            throw new ServerAnswerException();
//        }
//    }
//        @DeleteMapping("/{accountId}")
//    public ResponseEntity<Void> deleteAccount(
//            @PathVariable UUID accountId) {
//        accountUpdateService.deleteAccount(accountId);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/{accountId}")
//    public AccountInfoDto getAccount(
//            @PathVariable UUID accountId
//    ) {
//        return accountInfoService.getAccountFullInfo(accountId);
//    }
//
//    @GetMapping("/{accountId}/avatar")
//    public ResponseEntity<byte[]> getAvatar(
//            @PathVariable UUID accountId
//    ) {
//        byte[] avatar = accountInfoService.getAvatar(accountId);
//        if (avatar == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(avatar);
//    }
}
