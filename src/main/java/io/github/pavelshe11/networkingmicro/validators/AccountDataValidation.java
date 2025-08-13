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
    static int fieldMaxLength = 32;

    public List<ErrorDto> validateRegistrationData(Map<String, Object> userData) {
        List<ErrorDto> errors = new ArrayList<>();
        errors.addAll(validatePolitics(userData));
        errors.addAll(validateDomainName(userData));
        errors.addAll(validateFirstName(userData));
        errors.addAll(validateLastName(userData));
        return errors;
    }

    public List<ErrorDto> validatePolitics(Map<String, Object> userData) {
        List<ErrorDto> errors = new ArrayList<>();
        if (!Boolean.TRUE.equals(userData.get("acceptedPrivacyPolicy"))) {
            errors.add(new ErrorDto("acceptedPrivacyPolicy",
                    messageSource
                            .getMessage("not.accepted.privacy.policy",
                                    null,
                                    LocaleContextHolder.getLocale()))
            );
        }

        if (!Boolean.TRUE.equals(userData.get("acceptedPersonalDataProcessing"))) {
            errors.add(new ErrorDto("acceptedPersonalDataProcessing",
                    messageSource
                            .getMessage("not.accepted.personal.data.processing",
                                    null,
                                    LocaleContextHolder.getLocale()))
            );
        }

        return errors;
    }

    public List<ErrorDto> validateFirstName(Map<String, Object> userData) {
        List<ErrorDto> errors = new ArrayList<>();
        validateTextField("firstName", userData, errors);
        return errors;
    }

    public List<ErrorDto> validateLastName(Map<String, Object> userData) {
        List<ErrorDto> errors = new ArrayList<>();
        validateTextField("lastName", userData, errors);
        return errors;
    }

    private List<ErrorDto> validateEmptyField(String fieldName,
                                              String value, List<ErrorDto> errors) {

        if (value == null || value.isBlank()) {
            errors.add(new ErrorDto(
                            fieldName,
                            messageSource.getMessage("field.empty", null, LocaleContextHolder.getLocale())
                    )
            );
        }
        return errors;
    }

    private List<ErrorDto> validateTooLongField(String fieldName,
                                                String value, List<ErrorDto> errors) {

        if (value != null && value.length() > fieldMaxLength) {
            errors.add(new ErrorDto(
                            fieldName,
                            messageSource.getMessage("field.too.long", null, LocaleContextHolder.getLocale())
                    )
            );
        }
        return errors;
    }

    private List<ErrorDto> validateForbiddenSymbols(String fieldName,
                                                    String value, List<ErrorDto> errors) {

        if (value != null && !value.matches("^[a-zA-Zа-яА-ЯёЁ]+$")) {
            errors.add(new ErrorDto(
                            fieldName,
                            messageSource.getMessage("field.has.forbidden.symbols", null, LocaleContextHolder.getLocale())
                    )
            );
        }
        return errors;
    }

    private List<ErrorDto> validateTextField(String fieldName,
                                             Map<String, Object> userData, List<ErrorDto> errors) {
        Object value = userData.get(fieldName);
        List<ErrorDto> localErrors = new ArrayList<>();

        if (!(value instanceof String strValue)) {
            localErrors.add(new ErrorDto(
                    fieldName,
                    messageSource.getMessage("field.invalid.type", null, LocaleContextHolder.getLocale())
            ));
        } else {
            localErrors.addAll(validateEmptyField(fieldName, strValue, new ArrayList<>()));
            localErrors.addAll(validateTooLongField(fieldName, strValue, new ArrayList<>()));
            localErrors.addAll(validateForbiddenSymbols(fieldName, strValue, new ArrayList<>()));
        }

        errors.addAll(localErrors);
        return errors;
    }

    public Collection<ErrorDto> validateDomainName(Map<String, Object> userData) {
        if (!userData.containsKey("email")) return List.of();

        List<ErrorDto> errors = new ArrayList<>();

        Object emailObj = userData.get("email");
        if (!(emailObj instanceof String email) || !email.contains("@")) {
            return List.of();
        }

        String domain = email.substring(email.indexOf("@") + 1);
        boolean isDomainExists = !educationalInstitutionRepository.findAllByDomenName(domain).isEmpty();

        if (!isDomainExists) {
            errors.add(new ErrorDto(
                    "error",
                    messageSource.getMessage(
                            "institution.domain.not.registered", new Object[]{domain},
                            LocaleContextHolder.getLocale()
                    ))
            );
        }
        return errors;
    }

    public List<ErrorDto> validateUpdateData(Map<String, Object> updatedData) {
        List<ErrorDto> errors = new ArrayList<>();

        errors.addAll(validateTextField("firstName", updatedData, errors));
        errors.addAll(validateTextField("middleName", updatedData, errors));
        errors.addAll(validateTextField("lastName", updatedData, errors));
        errors.addAll(validateEmailField("email", updatedData, errors));
        errors.addAll(validateNumberField("courseNumber", updatedData, errors));
        errors.addAll(validateNumberField("dateOfBirth", updatedData, errors));
//        errors.addAll(validateBooleanField("professor", updatedData, errors));
//        errors.addAll(validateBooleanField("consulting", updatedData, errors));

        return errors;
    }

    private List<ErrorDto> validateBooleanField(String fieldName,
                                                Map<String, Object> userData,
                                                List<ErrorDto> errors) {

        return errors;
    }

    private List<ErrorDto> validateNumberField(String fieldName,
                                               Map<String, Object> userData,
                                               List<ErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!(value instanceof Integer intValue)) {
            errors.add(new ErrorDto(
                    fieldName,
                    messageSource.getMessage("field.invalid.type", null, LocaleContextHolder.getLocale())
            ));
            return errors;
        }
        if (intValue <= 0)
            errors.add(new ErrorDto(
                    fieldName,
                    messageSource.getMessage("field.invalid.type", null, LocaleContextHolder.getLocale())
            ));
        return errors;
    }

    private List<ErrorDto> validateEmailField(String fieldName,
                                              Map<String, Object> userData,
                                              List<ErrorDto> errors) {
        Object value = userData.get(fieldName);

        if (!(value instanceof String strValue)) {
            errors.add(new ErrorDto(
                    fieldName,
                    messageSource.getMessage("field.invalid.type", null, LocaleContextHolder.getLocale())
            ));
            return errors;
        }
        String emailPattern = "^[\\w-.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$";

        if (strValue == null || !strValue.matches(emailPattern)) {
            errors.add(
                    new ErrorDto(
                            fieldName,
                            messageSource.getMessage(
                                    "email.format.incorrect", null,
                                    LocaleContextHolder.getLocale()
                            ))
            );
        }
        return errors;
    }
}
