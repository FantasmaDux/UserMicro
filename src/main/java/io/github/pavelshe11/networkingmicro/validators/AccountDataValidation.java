package io.github.pavelshe11.networkingmicro.validators;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountDataValidation {
    private final EducationalInstitutionRepository educationalInstitutionRepository;

    public List<ErrorProto.FieldError> validateAll(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        errors.addAll(validatePolitics(userData));
        errors.addAll(validateDomenName(userData));
        return errors;
    }

    public List<ErrorProto.FieldError> validatePolitics(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        if (!userData.containsKey("acceptedPrivacyPolicy") || !userData.get("acceptedPrivacyPolicy").getBoolValue()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("acceptedPrivacyPolicy")
                    .setMessage("Не принято пользовательское соглашение.")
                    .build());
        }

        if (!userData.containsKey("acceptedPersonalDataProcessing") || !userData.get("acceptedPersonalDataProcessing").getBoolValue()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("acceptedPrivacyPolicy")
                    .setMessage("Не принято соглашение на обработку персональных данных.")
                    .build());
        }

        return errors;
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
                    .setMessage("Учебное заведение с доменом "
                            + domain + " не зарегистрировано в Communicator")
                    .build());
        }
        return errors;
    }

}
