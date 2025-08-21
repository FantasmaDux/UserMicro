package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.annotations.CommonApiResponses;
import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.AccountInactivityRequestDto;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Управление аккаунтом", description = "API для работы с аккаунтом пользователя")
@SecurityRequirement(name = "bearerTokenAuth")
@RequestMapping("/networking/v1/account")
public class AccountController {
    private final AccountUpdateService accountUpdateService;
    private final AccountInfoService accountInfoService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Метод обновления данных аккаунта пользователя по id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно обновлены"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверно указаны данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class),
                            examples = @ExampleObject(
                                    name = "BadRequestDetailed",
                                    summary = "Ошибка валидации",
                                    value = """
                                                        {
                                                        "error": "BadRequest",
                                              "detailedErrors": [
                                                {
                                                  "field": "dateOfBirth",
                                                  "message": "Неверный формат данных"
                                                },
                                                {
                                                  "field": "courseNumber",
                                                  "message": "Неверное значение поля"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @CommonApiResponses
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Список обновляемых полей аккаунта",
            required = false,
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
    @PatchMapping(value = "", produces = "application/json")
    public ResponseEntity<Void> updateAccountData(
            @Parameter(description = "Обновляемые поля аккаунта в формате JSON")
            @RequestBody Map<String, Object> updatedData) {
        UUID accountId = jwtUtil.claimAccountId();
        accountUpdateService.updateAccount(accountId, updatedData);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод обновления почты пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Код выслан на почту"),
            @ApiResponse(responseCode = "400", description = "Неверно указаны данные")
    })
    @PostMapping(value = "/email", produces = "application/json")
    public EmailUpdateResponseDto updateEmail(
            @RequestBody EmailUpdateRequestDto request) {
        UUID accountId = jwtUtil.claimAccountId();

        return accountUpdateService.updateEmail(request, accountId);
    }

    @Operation(summary = "Метод подтверждения обновления почты пользователя по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Почта успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Неверно указана почта или невалидный код")
    })
    @CommonApiResponses
    @PostMapping(value = "/confirmEmail", produces = "application/json")
    public ResponseEntity<Void> updateEmailConfirm(
            @RequestBody EmailUpdateConfirmRequestDto request) {
        UUID accountId = jwtUtil.claimAccountId();

        accountUpdateService.confirmEmail(request, accountId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод добавления/обновления аватара пользователя по id")
    @ApiResponse(responseCode = "200", description = "Аватар успешно обновлен")
    @CommonApiResponses
    @PostMapping(path = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
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

    @Operation(summary = "Настройка периода бездействия аккаунта для удаления по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Установлен период бездействия"),
            @ApiResponse(responseCode = "400", description = "Указано недопустимое время бездействия")
    })
    @CommonApiResponses
    @PatchMapping(value = "/set-inactivity", produces = "application/json")
    public ResponseEntity<Void> setInactivityDeletionPeriod(
            @RequestBody AccountInactivityRequestDto request
    ) {
        UUID accountId = jwtUtil.claimAccountId();
        accountUpdateService.setInactivityPeriod(accountId, request.getInactivityMonths());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Метод получения информации аккаунта пользователя по id")
    @ApiResponse(
            responseCode = "200",
            description = "Данные аккаунта получены",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountInfoDto.class)
            )
    )
    @CommonApiResponses
    @GetMapping(value = "", produces = "application/json")
    public AccountInfoDto getAccount() {
        UUID accountId = jwtUtil.claimAccountId();
        return accountInfoService.getAccountFullInfo(accountId);
    }

    @Operation(summary = "Метод получения аватара пользователя по id")
    @ApiResponse(responseCode = "200", description = "Аватар пользователя получен")
    @CommonApiResponses
    @GetMapping(value = "/avatar", produces = "image/jpeg")
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

}
