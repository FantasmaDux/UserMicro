package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountDataValidation {
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final MessageSource messageSource;
    private static final int FIELD_MAX_LENGTH = 32;
    private static final String ACCEPTABLE_SYMBOLS_PATTERN = "^[a-zA-Zа-яА-ЯёЁ]+$";
    private static final String EMAIL_PATTERN = "^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$";

    public Set<ErrorDto> validateRegistrationData(Map<String, Object> userData) {
        Set<ErrorDto> errors = new LinkedHashSet<>();

        validatePolitics(userData, errors);
        validateDomainName(userData, errors);
        validateFirstName(userData, errors);
        validateLastName(userData, errors);

        return errors;
    }

    public Set<ErrorDto> validateUpdateData(Map<String, Object> updatedData) {
        Set<ErrorDto> errors = new LinkedHashSet<>();

        validateTextField("firstName", updatedData, errors);
        validateTextField("middleName", updatedData, errors);
        validateTextField("lastName", updatedData, errors);
        validateEmailField("email", updatedData, errors);
        validateNumberField("courseNumber", updatedData, errors);
        validateNumberField("dateOfBirth", updatedData, errors);
//        validateBooleanField("professor", updatedData, errors);
//        validateBooleanField("consulting", updatedData, errors);

        return errors;
    }

    public void validatePolitics(Map<String, Object> userData, Set<ErrorDto> errors) {
        if (!Boolean.TRUE.equals(userData.get("acceptedPrivacyPolicy"))) {
            errors.add(createErrorDto("acceptedPrivacyPolicy", null,
                    "not.accepted.privacy.policy"));
        }

        if (!Boolean.TRUE.equals(userData.get("acceptedPersonalDataProcessing"))) {
            errors.add(createErrorDto("acceptedPersonalDataProcessing", null,
                    "not.accepted.personal.data.processing"));
        }
    }

    public void validateFirstName(Map<String, Object> userData, Set<ErrorDto> errors) {
        validateTextField("firstName", userData, errors);
    }

    public void validateLastName(Map<String, Object> userData, Set<ErrorDto> errors) {
        validateTextField("lastName", userData, errors);
    }

    private void validateEmptyField(String fieldName,
                                    String value, Set<ErrorDto> errors) {

        if (value == null || value.isBlank()) {
            errors.add(createErrorDto(fieldName, null,
                    "field.empty"));
        }
    }

    private void validateTooLongField(String fieldName,
                                      String value, Set<ErrorDto> errors) {

        if (value != null && value.length() > FIELD_MAX_LENGTH) {
            errors.add(createErrorDto(fieldName, null,
                    "field.too.long"));
        }
    }

    private void validateForbiddenSymbols(String fieldName,
                                          String value, Set<ErrorDto> errors) {

        if (value != null && !value.matches(ACCEPTABLE_SYMBOLS_PATTERN)) {
            errors.add(createErrorDto(fieldName, null,
                    "field.has.forbidden.symbols"));
        }
    }

    private void validateTextField(String fieldName,
                                   Map<String, Object> userData, Set<ErrorDto> errors) {
        Object value = userData.get(fieldName);

        if (!(value instanceof String strValue)) {
            errors.add(createErrorDto(fieldName, null, "field.invalid.type"));
            return;
        }

        validateEmptyField(fieldName, strValue, errors);
        validateTooLongField(fieldName, strValue, errors);
        validateForbiddenSymbols(fieldName, strValue, errors);
    }

    public void validateDomainName(Map<String, Object> userData, Set<ErrorDto> errors) {

        Object emailObj = userData.get("email");
        if (!(emailObj instanceof String email) || !email.contains("@")) {
            return;
        }

        String domain = email.substring(email.indexOf("@") + 1);
        boolean isDomainExists = !educationalInstitutionRepository.findAllByDomenName(domain).isEmpty();

        if (!isDomainExists) {
            errors.add(createErrorDto(
                    "error", new Object[]{domain}, "institution.domain.not.registered"
                    ));
        }
    }

    private void validateBooleanField(String fieldName,
                                      Map<String, Object> userData, Set<ErrorDto> errors) {

    }

    private void validateNumberField(String fieldName,
                                     Map<String, Object> userData, Set<ErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!(value instanceof Integer intValue) || intValue <= 0) {
            errors.add(createErrorDto(
                    fieldName, null, "field.invalid.type"));
        }
    }

    private void validateEmailField(String fieldName,
                                    Map<String, Object> userData, Set<ErrorDto> errors) {
        Object value = userData.get(fieldName);

        if (!(value instanceof String strValue) || !strValue.contains("@")) {
            errors.add(createErrorDto(
                    fieldName, null,
                    "field.invalid.type"));
            return;
        }

        if (!strValue.matches(EMAIL_PATTERN)) {
            errors.add(createErrorDto(
                    fieldName, null,
                    "email.format.incorrect"));
        }
    }

    private ErrorDto createErrorDto(String field, Object[] obj, String message) {
        return new ErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale())
        );
    }
}
