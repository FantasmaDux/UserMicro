package io.github.pavelshe11.networkingmicro.validators;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccountUpdateInfoValidator {
    private final CommonFieldsValidator commonFieldsValidator;

    public Set<FieldErrorDto> validateUpdateData(Map<String, Object> updatedData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        if (updatedData.containsKey("firstName")) {
            commonFieldsValidator.validateTextField("firstName", updatedData, errors);
        }
        if (updatedData.containsKey("middleName")) {
            commonFieldsValidator.validateTextField("middleName", updatedData, errors);
        }
        if (updatedData.containsKey("lastName")) {
            commonFieldsValidator.validateTextField("lastName", updatedData, errors);
        }
        if (updatedData.containsKey("courseNumber")) {
            commonFieldsValidator.validateCourseNumberField("courseNumber", updatedData, errors);
        }
        if (updatedData.containsKey("dateOfBirth")) {
            commonFieldsValidator.validateDateOfBirth("dateOfBirth", updatedData, errors);
        }
        if (updatedData.containsKey("dateOfEducationStart")) {
            commonFieldsValidator.validateDateOfEducationStart("dateOfEducationStart", updatedData, errors);
        }
        if (updatedData.containsKey("dateOfEducationEnd")) {
            commonFieldsValidator.validateDateOfEducationEnd("dateOfEducationEnd", updatedData, errors);
        }
        if (updatedData.containsKey("professor")) {
            commonFieldsValidator.validateBooleanField("professor", updatedData, errors);
        }
        if (updatedData.containsKey("consulting")) {
            commonFieldsValidator.validateBooleanField("consulting", updatedData, errors);
        }

        if (updatedData.containsKey("bio")) {
            commonFieldsValidator.validateBioField("bio", updatedData, errors);
        }

        return errors;
    }
}
