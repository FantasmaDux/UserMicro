package io.github.pavelshe11.networkingmicro.api.server.http.controllers;

import io.github.pavelshe11.networkingmicro.api.dto.requests.AvatarUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.services.AccountUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/networking/v1/account")
public class AccountController {
    private final AccountUpdateService accountUpdateService;

    // Передается мапа, так как при put с dto надо все поля задавать. С мапой можно передать на
    // обновление только часть полей
    @PutMapping("/{accountId}")
    public ResponseEntity<Void> updateAccountData(
            @PathVariable UUID accountId,
            @RequestBody Map<String, Object> updatedData) {
        accountUpdateService.updateAccount(accountId, updatedData);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{accountId}/email")
    public ResponseEntity<Void> updateEmail(
            @PathVariable UUID accountId,
            @RequestBody EmailUpdateRequestDto request) {
        accountUpdateService.updateEmail(request, accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/confirmEmail")
    public ResponseEntity<Void> updateEmailConfirm(
            @PathVariable UUID accountId,
            @RequestBody EmailUpdateConfirmRequestDto request) {
        accountUpdateService.confirmEmail(request, accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/{accountId}/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAvatar(
            @PathVariable UUID accountId,
            @ModelAttribute AvatarUpdateRequestDto request) {
        try {
            byte[] avatarBytes = request.getAvatar().getBytes();
            accountUpdateService.updateAvatar(accountId, avatarBytes);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ServerAnswerException();
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable UUID accountId) {
        accountUpdateService.deleteAccount(accountId);
        return ResponseEntity.ok().build();
    }
}
