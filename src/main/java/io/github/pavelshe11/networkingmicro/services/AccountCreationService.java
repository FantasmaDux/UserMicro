package io.github.pavelshe11.networkingmicro.services;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.api.dto.ErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.entities.EducationalInstitutionEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.github.pavelshe11.networkingmicro.validators.GrpcConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountCreationService {
    private final AccountRepository accountRepository;
    private final AccountDataValidation accountDataValidator;
    private final EducationalInstitutionRepository educationalInstitutionRepository;

    public AccountCreationProto.CreateAccountResponse createAccount(Map<String, Value> userData) {

        Map<String, Object> dataForValidation = GrpcConvertor.convertToObjectMap(userData);

        Set<FieldErrorDto> validationErrors = accountDataValidator.validateRegistrationData(dataForValidation);
        List<ErrorProto.FieldError> errors = validationErrors.stream()
                .map((FieldErrorDto) -> this.mapToProto(FieldErrorDto))
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Ошибка валидации данных")
                    .addAllDetailedErrors(errors)
                    .build();

            return AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();
        }

        try {
            String ip = userData.getOrDefault("ip", Value.newBuilder().setStringValue("").build()).getStringValue();
            String email = userData.getOrDefault("email", Value.newBuilder().setStringValue("").build()).getStringValue();
            boolean acceptedPrivacyPolicy = userData.getOrDefault("acceptedPrivacyPolicy", Value.newBuilder().setBoolValue(false).build()).getBoolValue();
            boolean acceptedPersonalDataProcessing = userData.getOrDefault("acceptedPersonalDataProcessing", Value.newBuilder().setBoolValue(false).build()).getBoolValue();
            String firstName = userData.getOrDefault("firstName", Value.newBuilder().setStringValue("").build()).getStringValue();
            String lastName = userData.getOrDefault("lastName", Value.newBuilder().setStringValue("").build()).getStringValue();
            String domain = email.substring(email.indexOf("@") + 1);
            EducationalInstitutionEntity educationalInstitution = educationalInstitutionRepository
                    .findByDomenName(domain)
                    .get();

            AccountEntity account = new AccountEntity();

            AccountContactInfoEntity emailContact = AccountContactInfoEntity.builder()
                    .contact(email)
                    .contactMethod(ContactMethodType.EMAIL)
                    .account(account)
                    .build();

            account.setMainEmailContact(emailContact);
            account.setAccountContactInfos(List.of(emailContact));
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setIp(ip);
            account.setAcceptedPrivacyPolicy(acceptedPrivacyPolicy);
            account.setAcceptedPersonalDataProcessing(acceptedPersonalDataProcessing);
            account.setEducationalInstitution(educationalInstitution);

            accountRepository.save(account);

            AccountCreationProto.SuccessResponse success =
                    AccountCreationProto.SuccessResponse.newBuilder()
                            .build();

            return AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setSuccess(success)
                    .build();

        } catch (Exception e) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Внутренняя ошибка сервера.")
                    .build();

            return AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();
        }
    }

    private ErrorProto.FieldError mapToProto(FieldErrorDto dto) {
        return ErrorProto.FieldError.newBuilder()
                .setField(dto.getField())
                .setMessage(dto.getMessage())
                .build();
    }
}