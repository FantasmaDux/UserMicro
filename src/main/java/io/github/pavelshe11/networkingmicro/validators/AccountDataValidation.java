package io.github.pavelshe11.networkingmicro.validators;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountDataValidation {
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final AccountRepository accountRepository;

    public List<ErrorProto.FieldError> validateEmail(Map<String, Value> userData) {
        List<ErrorProto.FieldError> errors = new ArrayList<>();
        if (!userData.containsKey("email") || userData.get("email").getStringValue().isBlank()) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("email")
                    .setMessage("Поле пустое")
                    .build());
        }
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
        String domain = email.substring(email.indexOf("@")+1);
        boolean isDomainExists = educationalInstitutionRepository.findByDomenName(domain).isPresent();

        if (!isDomainExists) {
            errors.add(ErrorProto.FieldError.newBuilder()
                    .setField("error")
                    .setMessage("Учебное заведение с доменом "
                            + domain + " не зарегистрировано в Communicator")
                    .build());
        }
        return errors;
    }

//    public Collection<ErrorProto.FieldError> validateAccountExisting(Map<String, Value> userData) {
//        if (!userData.containsKey("email")) return List.of();
//
//        List<ErrorProto.FieldError> errors = new ArrayList<>();
//
//        String email = userData.get("email").getStringValue();
//        UUID accountId = accountRepository.findByEmail(email)
//                .map(account -> account.getId())
//                .orElse(null);
//
//        if (accountId == null) {
//            errors.add(ErrorProto.FieldError.newBuilder()
//                    .setMessage("Сервер не отвечает.")
//                    .build());
//        }
//        return errors;
//    }
//
//    public Optional<UUID> getAccountIdIfExists(Map<String, Value> userData) {
//        if (!userData.containsKey("email")) return Optional.empty();
//        String email = userData.get("email").getStringValue();
//    }

    public Optional<UUID> getAccountIdIfExists(Map<String, Value> userData) {
        if (!userData.containsKey("email")) {
            return Optional.empty();
        }

        String email = userData.get("email").getStringValue();
        return accountRepository.findByEmail(email).map(account -> account.getId());

    }
}
