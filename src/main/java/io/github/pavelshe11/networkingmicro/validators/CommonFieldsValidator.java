package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.github.pavelshe11.networkingmicro.constants.ValidationConstants.*;

@Component
@RequiredArgsConstructor
public class CommonFieldsValidator {
    private final MessageSource messageSource;
    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private static final Logger log = LoggerFactory.getLogger(CommonFieldsValidator.class);

    private boolean isRequired(String fieldName) {
        return REQUIRED_FIELDS.contains(fieldName);
    }

    protected void validateCourseNumberField(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        validateNumberField(fieldName, updatedData, errors);
        Object value = updatedData.get(fieldName);
        int courseNumber;

        if (value instanceof Integer intValue) {
            courseNumber = intValue;
        } else {
            return;
        }

        if (courseNumber > 6) {
            errors.add(createFieldErrorDto(fieldName, null, "course.number.too.large"));
        }
    }

    protected void validateDateOfBirth(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
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

            if (date.isAfter(today)) {
                errors.add(createFieldErrorDto(fieldName, null, "date.of.birth.after.today"));
            }

        } catch (Exception e) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
        }
    }

    protected void validatePolitics(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        if (!Boolean.TRUE.equals(userData.get("acceptedPrivacyPolicy"))) {
            errors.add(createFieldErrorDto("acceptedPrivacyPolicy", null,
                    "not.accepted.privacy.policy"));
        }

        if (!Boolean.TRUE.equals(userData.get("acceptedPersonalDataProcessing"))) {
            errors.add(createFieldErrorDto("acceptedPersonalDataProcessing", null,
                    "not.accepted.personal.data.processing"));
        }
    }

    protected void validateFirstName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("firstName", userData, errors);
    }

    protected void validateLastName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("lastName", userData, errors);
    }

    protected void validateEmptyField(String fieldName,
                                    String value, Set<FieldErrorDto> errors) {

        boolean isRequired = isRequired(fieldName);

        if ((value == null || value.isBlank()) && isRequired) {
            errors.add(createFieldErrorDto(fieldName, null, "field.empty"));
        }

    }

    protected void validateTooLongField(String fieldName,
                                      String value, Set<FieldErrorDto> errors) {

        if (value != null && value.length() > FIELD_MAX_LENGTH) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.too.long"));
        }
    }

    protected void validateForbiddenSymbols(String fieldName,
                                          String value, Set<FieldErrorDto> errors) {

        if (value != null && !value.matches(ACCEPTABLE_SYMBOLS_PATTERN)) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.has.forbidden.symbols"));
        }
    }

    protected void validateTextField(String fieldName,
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

    protected void validateDomainName(String email, Set<FieldErrorDto> errors) {

        if (email == null || !email.contains("@")) {
            return;
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        boolean isDomainExists = educationalInstitutionRepository.findByDomenName(domain).isPresent();

        if (!isDomainExists) {
            errors.add(createFieldErrorDto(
                    "email", new Object[]{domain}, "institution.domain.not.registered"
            ));
        }
    }

    protected void validateBooleanField(String fieldName,
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

    protected void validateNumberField(String fieldName,
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

        EmailValidator validator = EmailValidator.getInstance(false, true);

        if (!validator.isValid(email)) {
            errors.add(createFieldErrorDto(
                    "email", null,
                    "email.format.incorrect"));
        }

        validateDomainName(email, errors);
    }

    protected FieldErrorDto createFieldErrorDto(String field, Object[] obj, String message) {
        return new FieldErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale())
        );
    }

    protected FieldErrorDto createFieldErrorDto(String field, Object[] obj, String message,
                                              UUID objectId) {
        return new FieldErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale()),
                objectId
        );
    }

    public boolean checkIfEmailFree(String email) {
        return accountRepository.findByMainEmailContactContact(email).isEmpty();
    }

    public void validateBioField(String bio, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        if (bio.length() > BIO_FIELD_MAX_LENGTH) {
            errors.add(new FieldErrorDto("bio", "Поле 'О себе' не должно превышать 100 символов"));
        }
    }
}
