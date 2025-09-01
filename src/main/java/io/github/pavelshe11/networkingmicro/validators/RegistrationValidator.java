package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RegistrationValidator {
    private final CommonFieldsValidator commonFieldsValidator;

    public Set<FieldErrorDto> validateRegistrationData(Map<String, Object> userData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        commonFieldsValidator.validatePolitics(userData, errors);
        String emailToValidate = null;
        Object emailObj = userData.get("email");
        if (emailObj instanceof String emailStr) {
            emailToValidate = emailStr;
        }
        commonFieldsValidator.validateEmailField(emailToValidate, errors);

        commonFieldsValidator.validateFirstName(userData, errors);
        commonFieldsValidator.validateLastName(userData, errors);

        return errors;
    }

}
