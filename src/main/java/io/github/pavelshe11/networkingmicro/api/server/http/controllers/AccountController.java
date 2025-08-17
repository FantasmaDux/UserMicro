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
    private final AccountInfoService accountInfoService;
    private final JwtUtil jwtUtil;

//    // Аналог метода с jwt извлечением id
//    @PatchMapping("")
//    public ResponseEntity<Void> updateAccountData(
//            @RequestBody Map<String, Object> updatedData) {
//        UUID accountId = jwtUtil.claimAccountId();
//        accountUpdateService.updateAccount(accountId, updatedData);
//        return ResponseEntity.ok().build();
//    }
//
//
//    // Аналог метода с jwt извлечением id
//    @PatchMapping(value = "/email")
//    public EmailUpdateResponseDto updateEmail(
//            @RequestBody EmailUpdateRequestDto request) {
//        UUID accountId = jwtUtil.claimAccountId();
//
//        return accountUpdateService.updateEmail(request, accountId);
//    }
//
//
//    // Аналог метода с jwt извлечением id
//    @PatchMapping("/confirmEmail")
//    public ResponseEntity<Void> updateEmailConfirm(
//            @RequestBody EmailUpdateConfirmRequestDto request) {
//        UUID accountId = jwtUtil.claimAccountId();
//
//        accountUpdateService.confirmEmail(request, accountId);
//        return ResponseEntity.ok().build();
//    }
//
//    // Аналог метода с jwt извлечением id
//    @PatchMapping(path = "/avatar",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Void> updateAvatar(
//            @ModelAttribute AvatarUpdateRequestDto request) {
//        try {
//            byte[] avatarBytes = request.getAvatar().getBytes();
//            UUID accountId = jwtUtil.claimAccountId();
//
//            accountUpdateService.updateAvatar(accountId, avatarBytes);
//            return ResponseEntity.ok().build();
//        } catch (IOException e) {
//            throw new ServerAnswerException();
//        }
//    }
//
//    // Аналог метода с jwt извлечением id
//    @DeleteMapping("")
//    public ResponseEntity<Void> deleteAccount() {
//        UUID accountId = jwtUtil.claimAccountId();
//        accountUpdateService.deleteAccount(accountId);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("")
//    public AccountInfoDto getAccount() {
//        UUID accountId = jwtUtil.claimAccountId();
//        return accountInfoService.getAccountFullInfo(accountId);
//    }
//
//    @GetMapping("/avatar")
//    public ResponseEntity<byte[]> getAvatar() {
//        UUID accountId = jwtUtil.claimAccountId();
//        byte[] avatar = accountInfoService.getAvatar(accountId);
//        if (avatar == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(avatar);
//    }

//     Передается мапа, так как при put с dto надо все поля задавать. С мапой можно передать на
//     обновление только часть полей
    @PatchMapping("/{accountId}")
    public ResponseEntity<Void> updateAccountData(
            @PathVariable UUID accountId,
            @RequestBody Map<String, Object> updatedData) {
        accountUpdateService.updateAccount(accountId, updatedData);
        return ResponseEntity.ok().build();
    }
    @PatchMapping(value = "/{accountId}/email")
    public EmailUpdateResponseDto updateEmail(
            @PathVariable UUID accountId,
            @RequestBody EmailUpdateRequestDto request) {

        return accountUpdateService.updateEmail(request, accountId);
    }
    @PatchMapping("/{accountId}/confirmEmail")
    public ResponseEntity<Void> updateEmailConfirm(
            @PathVariable UUID accountId,
            @RequestBody EmailUpdateConfirmRequestDto request) {
        accountUpdateService.confirmEmail(request, accountId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping(path = "/{accountId}/avatar",
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

    @GetMapping("/{accountId}")
    public AccountInfoDto getAccount(
            @PathVariable UUID accountId
    ) {
        return accountInfoService.getAccountFullInfo(accountId);
    }

    @GetMapping("/{accountId}/avatar")
    public ResponseEntity<byte[]> getAvatar(
            @PathVariable UUID accountId
    ) {
        byte[] avatar = accountInfoService.getAvatar(accountId);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(avatar);
    }
}
