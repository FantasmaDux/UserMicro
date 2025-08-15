package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.EmailUpdateResponseDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.*;
import io.github.pavelshe11.networkingmicro.component.CodeGenerator;
import io.github.pavelshe11.networkingmicro.normalization.DataNormalisation;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.CityEntity;
import io.github.pavelshe11.networkingmicro.store.entities.EmailUpdateSessionEntity;
import io.github.pavelshe11.networkingmicro.store.entities.SpecializationEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.CityRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EmailUpdateSessionRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.SpecializationRepository;
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
    private final SpecializationRepository specializationRepository;

    @Transactional
    public void updateAccount(UUID accountId, Map<String, Object> updatedData) {
        log.info("Начало обновления аккаунта: {}, данные: {}", accountId, updatedData);

        Map<String, Object> normalizedData = DataNormalisation.normalizeInput(updatedData);

        Set<FieldErrorDto> validationErrors = accountDataValidator.validateUpdateData(normalizedData);
        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        if (normalizedData.containsKey("firstName")) {
            account.setFirstName((String) normalizedData.get("firstName"));
        }

        if (normalizedData.containsKey("middleName")) {
            account.setMiddleName((String) (normalizedData.get("middleName")));
        }

        if (normalizedData.containsKey("lastName")) {
            account.setLastName((String) (normalizedData.get("lastName")));
        }

        if (normalizedData.containsKey("dateOfBirth")) {
            log.info("Обновление dateOfBirth");

            long dateOfBirthTimestamp = Long.parseLong((String) normalizedData.get("dateOfBirth")) * 1000;
            LocalDate dateOfBirth = Instant.ofEpochMilli(dateOfBirthTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            account.setDateOfBirth(dateOfBirth);
        }

        if (normalizedData.containsKey("idCity")) {
            UUID cityId = UUID.fromString(normalizedData.get("idCity").toString());
            Optional<CityEntity> cityOpt = cityRepository.findById(cityId);
            if (cityOpt.isEmpty()) {
                log.error("Нет города с таким id");
                throw new ServerAnswerException();
            }
            CityEntity city = cityOpt.get();
            account.setCity(city);
        }

        if (normalizedData.containsKey("idSpecialization")) {
            UUID cityId = UUID.fromString(normalizedData.get("idSpecialization").toString());
            Optional<SpecializationEntity> specializationOpt = specializationRepository.findById(cityId);
            if (specializationOpt.isEmpty()) {
                log.error("Нет специализации с таким id");
                throw new ServerAnswerException();
            }
            SpecializationEntity specialization = specializationOpt.get();
            account.setSpecialization(specialization);
        }

        if (normalizedData.containsKey("isProfessor")) {
            account.setProfessor((Boolean) normalizedData.get("isProfessor"));
        }

        if (normalizedData.containsKey("isConsulting")) {
            account.setConsulting((Boolean) normalizedData.get("isConsulting"));
        }

        if (normalizedData.containsKey("courseNumber")) {
            log.info("Обновление courseNumber");
            Object courseNumberObj = normalizedData.get("courseNumber");
            if (courseNumberObj instanceof Integer courseNum) {
                account.setCourseNumber(courseNum.shortValue());
            } else if (courseNumberObj instanceof String courseNumStr) {
                short parsedShort = Short.parseShort(courseNumStr);
                account.setCourseNumber(parsedShort);
            }
        }

        log.info("Сохранение аккаунта {}", accountId);
        accountRepository.save(account);
    }

    @Transactional
    public EmailUpdateResponseDto updateEmail(EmailUpdateRequestDto request, UUID accountId) {
        Set<FieldErrorDto> validationErrors = new HashSet<>();
        accountDataValidator.validateEmailField(request.getEmail(), validationErrors);
        accountDataValidator.checkIfEmailFreeOrThrow(request.getEmail());

        boolean accountExists = accountRepository.existsById(accountId);
        if (!accountExists) {
            throw new ServerAnswerException();
        }

        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findByAccountId(accountId);
        EmailUpdateResponseDto emailUpdateResponse;

        if (sessionOpt.isPresent()) {
            emailUpdateResponse = handleExistingSession(sessionOpt.get(), request.getEmail(), accountId);
        } else {
            emailUpdateResponse = handleNewSession(request.getEmail(), accountId);
        }

        return emailUpdateResponse;
    }

    @Transactional
    public void confirmEmail(EmailUpdateConfirmRequestDto request, UUID accountId) {

        String code = securityValidator.getTrimmedCodeOrThrow(request.getCode());
        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findByAccountId(accountId);
        if (sessionOpt.isEmpty()) {
            log.error("Нет такой сессии.");
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
            throw new InvalidCodeException();
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

    private EmailUpdateResponseDto handleNewSession(String email, UUID accountId) {

        String rawCode = codeGenerator.codeGenerate();
        log.info("UPDATE_EMAIL_CODE: " + rawCode + " NEW EMAIL: " + email + " OLD EMAIL" +
                accountRepository.findById(accountId).get().getEmail());
        String hashCode = codeGenerator.codeHash(rawCode);
        long codeExpires = codeGenerator.codeExpiresGenerate();

        EmailUpdateSessionEntity emailUpdateSession =
        EmailUpdateSessionEntity.builder()
                .newEmail(email)
                .code(hashCode)
                .accountId(accountId)
                .codeExpires(new Timestamp(codeExpires))
                .build();

        emailUpdateSessionRepository.save(emailUpdateSession);

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

            log.info("UPDATE_EMAIL_CODE: " + rawRefreshCode + " NEW EMAIL: " + email + " OLD EMAIL" +
                    accountRepository.findById(accountId).get().getEmail());
            emailUpdateSessionRepository.save(session);

            return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), refreshCodeExpires);
        }

        return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), session.getCodeExpires().getTime());
    }
}
