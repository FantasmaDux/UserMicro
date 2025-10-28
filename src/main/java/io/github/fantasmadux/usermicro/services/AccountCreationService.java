package io.github.fantasmadux.usermicro.services;

import com.google.protobuf.Value;
import io.github.fantasmadux.usermicro.grpc.AccountCreationProto;
import io.github.fantasmadux.usermicro.grpc.ErrorProto;
import io.github.fantasmadux.usermicro.api.dto.FieldErrorDto;
import io.github.fantasmadux.usermicro.store.entities.AccountContactInfoEntity;
import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.entities.ActivitySessionEntity;
import io.github.fantasmadux.usermicro.store.entities.EducationalInstitutionEntity;
import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import io.github.fantasmadux.usermicro.store.repositories.ActivitySessionRepository;
import io.github.fantasmadux.usermicro.store.repositories.EducationalInstitutionRepository;
import io.github.fantasmadux.usermicro.util.HelperUtils;
import io.github.fantasmadux.usermicro.validators.GrpcConvertor;
import io.github.fantasmadux.usermicro.validators.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountCreationService {
    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final RegistrationValidator registrationValidator;
    private final ActivitySessionRepository activitySessionRepository;
    private final AccountCleanerService accountCleanerService;

    public AccountCreationProto.CreateAccountResponse createAccount(Map<String, Value> userData) {

        Map<String, Object> dataForValidation = GrpcConvertor.convertToObjectMap(userData);

        Set<FieldErrorDto> validationErrors = registrationValidator.validateRegistrationData(dataForValidation);
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
                    .iconUrl(HelperUtils.fetchFaviconUrl(email))
                    .modifiable(false)
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

            ActivitySessionEntity activitySession = ActivitySessionEntity.builder()
                    .account(account)
                    .lastActivity(Timestamp.from(Instant.now()))
                    .build();

            activitySessionRepository.save(activitySession);

            accountCleanerService.cleanInactiveAccounts(
                    account.getId(),
                    activitySession.getLastActivity().getTime() + activitySession.getInactivityTimeMs()
            );

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