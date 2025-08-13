package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.AccountDeleteException;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountUpdateService {
    private final AccountRepository accountRepository;
    private final AccountDataValidation accountDataValidator;

    public void updateAccount(UUID accountId, Map<String, Object> updatedData) {
        List<ErrorDto> validationErrors = accountDataValidator.validateUpdateData(updatedData);
        if (!validationErrors.isEmpty()) {
            throw new ServerAnswerException();
        }

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ServerAnswerException());

        if (updatedData.containsKey("firstName")) {
            account.setFirstName((String) updatedData.get("firstName"));
        }

        if (updatedData.containsKey("middleName")) {
            account.setFirstName((String) updatedData.get("middleName"));
        }

        if (updatedData.containsKey("lastName")) {
            account.setFirstName((String) updatedData.get("lastName"));
        }

        accountRepository.save(account);
    }

    public void updateEmail(EmailUpdateRequestDto request, UUID accountId) {
    }

    public void confirmEmail(EmailUpdateConfirmRequestDto request, UUID accountId) {
    }

    public void updateAvatar(UUID accountId, byte[] avatarBytes) {
    }

    public void deleteAccount(UUID accountId) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new AccountDeleteException();
        }

        accountRepository.delete(accountOpt.get());
    }
}
