package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateConfirmRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.EmailUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.responses.EmailUpdateResponseDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.*;
import io.github.pavelshe11.networkingmicro.component.CodeGenerator;
import io.github.pavelshe11.networkingmicro.normalization.DataNormalisation;
import io.github.pavelshe11.networkingmicro.store.entities.*;
import io.github.pavelshe11.networkingmicro.store.enums.MediaType;
import io.github.pavelshe11.networkingmicro.store.repositories.*;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.github.pavelshe11.networkingmicro.validators.SecurityValidation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;

import java.io.IOException;
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
    private final ActivitySessionRepository activitySessionRepository;
    private static final int MAX_AVATAR_SIZE_BYTES = 5 * 1024 * 1024;

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

            Object dobRaw = normalizedData.get("dateOfBirth");

            long dateOfBirthTimestamp = dobRaw instanceof Number
                    ? ((Number) dobRaw).longValue()
                    : Long.parseLong(dobRaw.toString());

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

        if (!validationErrors.isEmpty()) {
            throw new FieldValidationException("email", validationErrors.stream().toList());
        }

        Optional<EmailUpdateSessionEntity> sessionOptByEmail
                = emailUpdateSessionRepository.findByNewEmail(request.getEmail());

        boolean isAccountFree = accountDataValidator.checkIfEmailFree(request.getEmail());
        boolean isAccountUsedInAnotherSession = sessionOptByEmail
                .map(session -> session.getAccountId().equals(accountId))
                .orElse(false);
        boolean isFake = !isAccountFree || isAccountUsedInAnotherSession;

        boolean accountExists = accountRepository.existsById(accountId);
        if (!accountExists) {
            throw new ServerAnswerException();
        }

        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findByAccountId(accountId);
        EmailUpdateResponseDto emailUpdateResponse;

        if (sessionOpt.isPresent()) {
            emailUpdateResponse = handleExistingSession(sessionOpt.get(), request.getEmail(), accountId, isFake);
        } else if (isAccountFree) {
            emailUpdateResponse = handleNewSession(request.getEmail(), accountId, isFake);
        } else {
            emailUpdateResponse = handleNewSession(request.getEmail(), accountId, isFake);
        }

        return emailUpdateResponse;
    }

    @Transactional
    public void confirmEmail(EmailUpdateConfirmRequestDto request, UUID accountId) {

        Set<FieldErrorDto> validationErrors = new HashSet<>();
        accountDataValidator.validateEmailField(request.getEmail(), validationErrors);

        if (!validationErrors.isEmpty()) {
            throw new FieldValidationException("email", validationErrors.stream().toList());
        }

        String code = securityValidator.getTrimmedCodeOrThrow(request.getCode());
        Optional<EmailUpdateSessionEntity> sessionOpt = emailUpdateSessionRepository.findByAccountId(accountId);
        if (sessionOpt.isEmpty()) {
            log.error("Нет такой сессии.");
            throw new ServerAnswerException();
        }

        EmailUpdateSessionEntity session = sessionOpt.get();

        boolean isSessionFake = session.getCode().isEmpty();

        if (isSessionFake) {
            log.warn("Фейковая сессия создана.");
            throw new InvalidCodeException();
        }

        if (!request.getEmail().equals(session.getNewEmail())) {
            log.error("Почта для изменения не совпадает с текущей.");
            throw new EmailEqualsException();
        }

        securityValidator.checkIfCodeIsValid(session, code);
        securityValidator.ensureCodeIsNotExpired(session);

        boolean isEmailStillFree = accountDataValidator.checkIfEmailFree(request.getEmail());
        if (!isEmailStillFree) {
            log.info("Почта уже занята.");
            throw new InvalidCodeException();
        }

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
    public void updateAvatar(UUID accountId, MultipartFile avatarFile) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        if (avatarFile == null || avatarFile.isEmpty()) {
            log.error("Аватара нет.");
            throw new ServerAnswerException();
        }

        if (avatarFile.getSize() > MAX_AVATAR_SIZE_BYTES) {
            log.error("Аватар больше 5 Мб.");
            throw new AvatarLargeSizeException();
        }

        String mimeType;
        try {
            Tika tika = new Tika();
            mimeType = tika.detect(avatarFile.getBytes());

            MediaType mediaType = MediaType.fromMimeType(mimeType);

            if (mediaType == null) {
                log.error("Недопустимый MIME-тип: " + mimeType);
                throw new InvalidMimeTypeException();
            }

            AccountEntity account = accountOpt.get();
            account.setAvatar(avatarFile.getBytes());
            account.setMimetype(mediaType);
        } catch (IOException e) {
            log.error("Тип mimeType не удалось определить " + e);
            throw new ServerAnswerException();
        }
    }

    private EmailUpdateResponseDto handleNewSession(String email, UUID accountId, boolean isFake) {

        emailUpdateSessionRepository.deleteByAccountId(accountId);

        String rawCode = isFake ? "" : codeGenerator.codeGenerate();

        log.info("{}_UPDATE_EMAIL_CODE: {} NEW EMAIL: {} OLD EMAIL: {}",
                isFake ? "FAKE" : "REAL",
                rawCode, email,
                accountRepository.findById(accountId).get().getEmail());

        String hashCode = isFake ? "" : codeGenerator.codeHash(rawCode);
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

    private EmailUpdateResponseDto handleExistingSession(EmailUpdateSessionEntity session,
                                                         String email,
                                                         UUID accountId,
                                                         boolean isFake) {
        boolean isExpired = session.getCodeExpires().before(Timestamp.from(Instant.now()));
        boolean isSameAccount = accountId.equals(session.getAccountId());
        boolean isSameEmail = email.equals(session.getNewEmail());
        boolean wasSessionFake = session.getCode().isEmpty();

        if (isExpired || !isSameAccount || !isSameEmail || (wasSessionFake && !isFake)) {
            return handleNewSession(email, accountId, isFake);
        }

        return new EmailUpdateResponseDto(codeGenerator.getCodePattern(), session.getCodeExpires().getTime());
    }

    public void setInactivityPeriod(UUID accountId, int inactivityMonths) {
        if (inactivityMonths > 6 || inactivityMonths <= 0) {
            throw new SetInactivityMonthException();
        }

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            log.error("Аккаунт при установке периода бездействия не найден");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        ActivitySessionEntity activitySession = activitySessionRepository.findByAccount(account)
                .orElse(ActivitySessionEntity.builder()
                        .account(account)
                        .lastActivity(Timestamp.from(Instant.now()))
                        .inactivityMonths(inactivityMonths)
                        .build()
                );

        if (activitySession.getId() != null) {
            activitySession.setInactivityMonths(inactivityMonths);
        }

        activitySessionRepository.save(activitySession);
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);

        if (accountOpt.isEmpty()) {
            throw new AccountDeleteException();
        }

        accountRepository.delete(accountOpt.get());
    }
}
