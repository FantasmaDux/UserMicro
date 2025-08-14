package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.EmailUpdateResponseDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.AccountDeleteException;
import io.github.pavelshe11.networkingmicro.api.exceptions.EmailEqualsException;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.component.CodeGenerator;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.CityEntity;
import io.github.pavelshe11.networkingmicro.store.entities.EmailUpdateSessionEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.CityRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EmailUpdateSessionRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.github.pavelshe11.networkingmicro.validators.SecurityValidation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class AccountUpdateService {
    private final AccountRepository accountRepository;
    private final AccountDataValidation accountDataValidator;
    private final CityRepository cityRepository;
    private final EmailUpdateSessionRepository emailUpdateSessionRepository;
    private final CodeGenerator codeGenerator;
    private final SecurityValidation securityValidator;
    private static final Logger log = LoggerFactory.getLogger(AccountUpdateService.class);

    @Transactional
    public void updateAccount(UUID accountId, Map<String, Object> updatedData) {
        Set<ErrorDto> validationErrors = accountDataValidator.validateUpdateData(updatedData);
        if (!validationErrors.isEmpty()) {
            throw new ServerAnswerException();
        }

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        if (updatedData.containsKey("firstName")) {
            account.setFirstName((String) updatedData.get("firstName"));
        }

        if (updatedData.containsKey("middleName")) {
            account.setMiddleName((String) updatedData.get("middleName"));
        }

        if (updatedData.containsKey("lastName")) {
            account.setLastName((String) updatedData.get("lastName"));
        }

        if (updatedData.containsKey("dateOfBirth")) {
            long dateOfBirthTimestamp = Long.parseLong((String) updatedData.get("dateOfBirth"));
            LocalDate dateOfBirth = Instant.ofEpochMilli(dateOfBirthTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            account.setDateOfBirth(dateOfBirth);
        }

        if (updatedData.containsKey("idCity")) {
            UUID cityId = UUID.fromString(updatedData.get("idCity").toString());
            CityEntity city = cityRepository.findById(cityId).orElseThrow(() -> new ServerAnswerException());
            account.setCity(city);
        }

        if (updatedData.containsKey("idSpecialisation")) {
            UUID cityId = UUID.fromString(updatedData.get("idCity").toString());
            CityEntity city = cityRepository.findById(cityId).orElseThrow(() -> new ServerAnswerException());
            account.setCity(city);
        }

        if (updatedData.containsKey("isProfessor")) {
            account.setProfessor((Boolean) updatedData.get("isProfessor"));
        }

        if (updatedData.containsKey("isConsulting")) {
            account.setConsulting((Boolean) updatedData.get("isConsulting"));
        }

        if (updatedData.containsKey("courseNumber")) {
            account.setCourseNumber((Short) updatedData.get("courseNumber"));
        }

        accountRepository.save(account);
    }

    @Transactional
    public EmailUpdateResponseDto updateEmail(EmailUpdateRequestDto request, UUID accountId) {
        Set<ErrorDto> validationErrors = new HashSet<>();
        accountDataValidator.validateEmailField(request.getEmail(), validationErrors);
        accountDataValidator.checkIfEmailFreeOrThrow(request.getEmail());

        boolean accountExists = accountRepository.existsById(accountId);
        if (!accountExists) {
            throw new ServerAnswerException();
        }

        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findById(accountId);
        EmailUpdateResponseDto emailUpdateResponse;

        if (sessionOpt.isEmpty()) {
            emailUpdateResponse = handleExistingSession(sessionOpt.get(), request.getEmail(), accountId);
        } else {
            emailUpdateResponse = handleNewSession(sessionOpt.get(), request.getEmail());
        }

        return emailUpdateResponse;
    }

    @Transactional
    public void confirmEmail(EmailUpdateConfirmRequestDto request, UUID accountId) {

        String code = securityValidator.getTrimmedCodeOrThrow(request.getCode());
        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findById(accountId);
        if (sessionOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        EmailUpdateSessionEntity session = sessionOpt.get();

        if (!request.getEmail().equals(session.getNewEmail())) {
            log.error("Почта для изменения не совпадает с текущей.");
            throw new EmailEqualsException();
        }

        securityValidator.checkIfCodeIsValid(session, code);
        securityValidator.ensureCodeIsNotExpired(session);

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();
        account.setEmail(request.getEmail());

        accountRepository.save(account);
        emailUpdateSessionRepository.deleteById(accountId);
    }

    @Transactional
    public void updateAvatar(UUID accountId, byte[] avatarBytes) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();
        account.setAvatar(avatarBytes);
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new AccountDeleteException();
        }

        accountRepository.delete(accountOpt.get());
    }

    private EmailUpdateResponseDto handleNewSession(EmailUpdateSessionEntity session, String email) {

        String rawCode = codeGenerator.codeGenerate();
        log.info("UPDATE_EMAIL_CODE: " + rawCode + " FOR EMAIL: " + email);
        String hashCode = codeGenerator.codeHash(rawCode);
        long codeExpires = codeGenerator.codeExpiresGenerate();

        EmailUpdateSessionEntity.builder()
                .newEmail(email)
                .code(hashCode)
                .codeExpires(new Timestamp(codeExpires))
                .build();

        emailUpdateSessionRepository.save(session);

        return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), codeExpires);

    }

    private EmailUpdateResponseDto handleExistingSession(EmailUpdateSessionEntity session, String email, UUID accountId) {
        boolean isExpired = session.getCodeExpires().before(Timestamp.from(Instant.now()));

        if (isExpired || session.getAccountId() == null
                || !session.getAccountId().equals(accountId)) {
            String rawRefreshCode = codeGenerator.codeGenerate();
            String hashedRefreshCode = codeGenerator.codeHash(rawRefreshCode);
            long refreshCodeExpires = codeGenerator.codeExpiresGenerate();

            session.setAccountId(accountId);
            session.setCode(hashedRefreshCode);
            session.setCodeExpires(new Timestamp(refreshCodeExpires));

            log.info("UPDATE_EMAIL_CODE: " + rawRefreshCode + " FOR EMAIL: " + email);
            emailUpdateSessionRepository.save(session);

            return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), refreshCodeExpires);
        }

        return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), session.getCodeExpires().getTime());
    }
}
