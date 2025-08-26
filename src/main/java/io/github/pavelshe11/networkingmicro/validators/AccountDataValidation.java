package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.services.AccountUpdateService;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountDataValidation {
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final MessageSource messageSource;
    private final AccountRepository accountRepository;
    private static final Logger log = LoggerFactory.getLogger(AccountDataValidation.class);

    private static final int FIELD_MAX_LENGTH = 32;
    private static final String ACCEPTABLE_SYMBOLS_PATTERN = "^[a-zA-Zа-яА-ЯёЁ]+$";
    private static final String EMAIL_PATTERN = "^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$";

    private static final Set<String> REQUIRED_FIELDS = Set.of(
            "firstName", "email", "isProfessor", "isAdmin", "isVisible", "isConsulting"
    );

    private boolean isRequired(String fieldName) {
        return REQUIRED_FIELDS.contains(fieldName);
    }

    public Set<FieldErrorDto> validateRegistrationData(Map<String, Object> userData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        validatePolitics(userData, errors);
        validateDomainName(userData, errors);
        String emailToValidate = null;
        Object emailObj = userData.get("email");
        if (emailObj instanceof String emailStr) {
            emailToValidate = emailStr;
        }
        validateEmailField(emailToValidate, errors);
        validateFirstName(userData, errors);
        validateLastName(userData, errors);

        return errors;
    }

    public Set<FieldErrorDto> validateUpdateData(Map<String, Object> updatedData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        if (updatedData.containsKey("firstName")) {
            validateTextField("firstName", updatedData, errors);
        }
        if (updatedData.containsKey("middleName")) {
            validateTextField("middleName", updatedData, errors);
        }
        if (updatedData.containsKey("lastName")) {
            validateTextField("lastName", updatedData, errors);
        }
        if (updatedData.containsKey("courseNumber")) {
            validateCourseNumberField("courseNumber", updatedData, errors);
        }
        if (updatedData.containsKey("dateOfBirth")) {
            validateDateOfBirth("dateOfBirth", updatedData, errors);
        }
        if (updatedData.containsKey("professor")) {
            validateBooleanField("professor", updatedData, errors);
        }
        if (updatedData.containsKey("consulting")) {
            validateBooleanField("consulting", updatedData, errors);
        }

        return errors;
    }

    private void validateCourseNumberField(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        validateNumberField(fieldName, updatedData, errors);
        Object value = updatedData.get(fieldName);
        int courseNumber;

        if (value instanceof Integer intValue) {
            courseNumber = intValue;
        } else {
            return;
        }

        if (courseNumber > 5) {
            errors.add(createFieldErrorDto(fieldName, null, "course.number.too.large"));
        }
    }

    private void validateDateOfBirth(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        if (!updatedData.containsKey(fieldName)) {
            return;
        }

        Object value = updatedData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (!(value instanceof Number)) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
            return;
        }

        try {

            long timestamp = ((Number) value).longValue();
            LocalDate date = Instant.ofEpochMilli(timestamp * 1000)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate today = LocalDate.now();
            LocalDate minDate = today.minusYears(100);

            if (date.isAfter(today)) {
                errors.add(createFieldErrorDto(fieldName, null, "date.of.birth.after.today"));
            } else if (date.isBefore(minDate)) {
                errors.add(createFieldErrorDto(fieldName, null, "date.of.birth.too.old"));
            }

        } catch (Exception e) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
        }
    }

    public void validatePolitics(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        if (!Boolean.TRUE.equals(userData.get("acceptedPrivacyPolicy"))) {
            errors.add(createFieldErrorDto("acceptedPrivacyPolicy", null,
                    "not.accepted.privacy.policy"));
        }

        if (!Boolean.TRUE.equals(userData.get("acceptedPersonalDataProcessing"))) {
            errors.add(createFieldErrorDto("acceptedPersonalDataProcessing", null,
                    "not.accepted.personal.data.processing"));
        }
    }

    public void validateFirstName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("firstName", userData, errors);
    }

    public void validateLastName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("lastName", userData, errors);
    }

    private void validateEmptyField(String fieldName,
                                    String value, Set<FieldErrorDto> errors) {

        boolean isRequired = isRequired(fieldName);

        if ((value == null || value.isBlank()) && isRequired) {
            errors.add(createFieldErrorDto(fieldName, null, "field.empty"));
        }

    }

    private void validateTooLongField(String fieldName,
                                      String value, Set<FieldErrorDto> errors) {

        if (value != null && value.length() > FIELD_MAX_LENGTH) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.too.long"));
        }
    }

    private void validateForbiddenSymbols(String fieldName,
                                          String value, Set<FieldErrorDto> errors) {

        if (value != null && !value.matches(ACCEPTABLE_SYMBOLS_PATTERN)) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.has.forbidden.symbols"));
        }
    }

    private void validateTextField(String fieldName,
                                   Map<String, Object> userData, Set<FieldErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (!(value instanceof String strValue)) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
            return;
        }

        validateEmptyField(fieldName, strValue, errors);

        if (strValue.isEmpty()) {
            return;
        }

        validateTooLongField(fieldName, strValue, errors);
        validateForbiddenSymbols(fieldName, strValue, errors);
    }

    public void validateDomainName(Map<String, Object> userData, Set<FieldErrorDto> errors) {

        Object emailObj = userData.get("email");
        if (!(emailObj instanceof String email) || !email.contains("@")) {
            return;
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        boolean isDomainExists = educationalInstitutionRepository.findByDomenName(domain).isPresent();

        if (!isDomainExists) {
            errors.add(createFieldErrorDto(
                    "error", new Object[]{domain}, "institution.domain.not.registered"
            ));
        }
    }

    private void validateBooleanField(String fieldName,
                                      Map<String, Object> userData, Set<FieldErrorDto> errors) {
        if (!userData.containsKey(fieldName)) {
            return;
        }

        Object value = userData.get(fieldName);

        if (value instanceof Boolean) {
            return;
        }
        if (value instanceof String) {
            String strValue = ((String) value).toLowerCase(Locale.ROOT);
            if (strValue.equals("true") || strValue.equals("false")) {
                return;
            }
        }

        errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
    }

    private void validateNumberField(String fieldName,
                                     Map<String, Object> userData, Set<FieldErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (value instanceof Integer intValue) {
            if (intValue < 0) {
                errors.add(createFieldErrorDto(fieldName, null, "field.invalid.value"));
            }
            return;
        }
        errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
    }

    public void validateEmailField(String email, Set<FieldErrorDto> errors) {

        if (email == null || email.isBlank()) {
            errors.add(createFieldErrorDto(
                    "email", null,
                    "field.empty"));
            return;
        }

        if (!email.matches(EMAIL_PATTERN)) {
            errors.add(createFieldErrorDto(
                    "email", null,
                    "email.format.incorrect"));
        }
    }

    private FieldErrorDto createFieldErrorDto(String field, Object[] obj, String message) {
        return new FieldErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale())
        );
    }

    public boolean checkIfEmailFree(String email) {
        return accountRepository.findByEmail(email).isEmpty();
    }
}
