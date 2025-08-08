package io.github.pavelshe11.networkingmicro.validators;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.ErrorProto;
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

    public List<ErrorProto.FieldError> validateAll(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        errors.addAll(validatePolitics(userData));
        errors.addAll(validateDomenName(userData));
        errors.addAll(validateFirstName(userData));
        errors.addAll(validateMiddleName(userData));
        errors.addAll(validateLastName(userData));
        return errors;
    }

    public List<ErrorProto.FieldError> validatePolitics(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        if (!userData.containsKey("acceptedPrivacyPolicy") || !userData.get("acceptedPrivacyPolicy").getBoolValue()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("acceptedPrivacyPolicy")
                    .setMessage(messageSource.getMessage("not.accepted.privacy.policy", null, LocaleContextHolder.getLocale()))
                    .build());
        }

        if (!userData.containsKey("acceptedPersonalDataProcessing") || !userData.get("acceptedPersonalDataProcessing").getBoolValue()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("acceptedPrivacyPolicy")
                    .setMessage(messageSource.getMessage("not.accepted.personal.data.processing", null, LocaleContextHolder.getLocale()))
                    .build());
        }

        return errors;
    }

    public List<ErrorProto.FieldError> validateFirstName(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        validateEmptyField("firstName", userData, errors);
        return errors;
    }

    public List<ErrorProto.FieldError> validateMiddleName(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        validateEmptyField("middleName", userData, errors);
        return errors;
    }

    public List<ErrorProto.FieldError> validateLastName(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        validateEmptyField("lastName", userData, errors);
        return errors;
    }

    private void validateEmptyField(String fieldName,
                                    Map<String, Value> userData, List<ErrorProto.FieldError> errors) {
        if (!userData.containsKey(fieldName) ||
                userData.get(fieldName).getStringValue().trim().isEmpty()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField(fieldName)
                    .setMessage(messageSource.getMessage("field.empty", null, LocaleContextHolder.getLocale())
                    )
                    .build()
            );
        }
    }

    public Collection<ErrorProto.FieldError> validateDomenName(Map<String, Value> userData) {
        if (!userData.containsKey("email")) return List.of();

        List<ErrorProto.FieldError> errors = new ArrayList<>();

        String email = userData.get("email").getStringValue();
        String domain = email.substring(email.indexOf("@") + 1);
        boolean isDomainExists = !educationalInstitutionRepository.findAllByDomenName(domain).isEmpty();

        if (!isDomainExists) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("error")
                    .setMessage(
                            messageSource.getMessage(
                                    "institution.domain.not.registered", new Object[]{domain},
                                    LocaleContextHolder.getLocale()
                            )
                    )
                    .build());
        }
        return errors;
    }

}
