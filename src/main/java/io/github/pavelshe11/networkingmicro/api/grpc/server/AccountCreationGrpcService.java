package io.github.pavelshe11.networkingmicro.api.grpc.server;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.AccountCreationServiceGrpc;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@GrpcService
@RequiredArgsConstructor
public class AccountCreationGrpcService extends AccountCreationServiceGrpc.AccountCreationServiceImplBase {

    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final AccountDataValidation accountDataValidator;


    @Override
    public void createAccount(AccountCreationProto.CreateAccountRequest request, StreamObserver<AccountCreationProto.CreateAccountResponse> responseObserver) {

        Map<String, Value> userData = request.getUserDataMap();
        List<ErrorProto.FieldError> errors = accountDataValidator.validateAll(userData);


        if (!errors.isEmpty()) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Ошибка валидации данных")
                    .addAllDetailedErrors(errors)
                    .build();

            AccountCreationProto.CreateAccountResponse response = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        try {
            String ip = userData.getOrDefault("ip", Value.newBuilder().setStringValue("").build()).getStringValue();
            String email = userData.getOrDefault("email", Value.newBuilder().setStringValue("").build()).getStringValue();
            boolean acceptedPrivacyPolicy = userData.getOrDefault("acceptedPrivacyPolicy", Value.newBuilder().setBoolValue(false).build()).getBoolValue();
            boolean acceptedPersonalDataProcessing = userData.getOrDefault("acceptedPersonalDataProcessing", Value.newBuilder().setBoolValue(false).build()).getBoolValue();

            String domenNameOfEmail = email.substring(email.indexOf("@") + 1);

            var educationalInstitution = educationalInstitutionRepository.findByDomenName(domenNameOfEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Домен не найден: " + domenNameOfEmail));

            AccountEntity account = new AccountEntity();
            account.setEmail(email);
            account.setIp(ip);
            account.setAcceptedPrivacyPolicy(acceptedPrivacyPolicy);
            account.setAcceptedPersonalDataProcessing(acceptedPersonalDataProcessing);

            accountRepository.save(account);

            AccountCreationProto.SuccessResponse success
                    = AccountCreationProto.SuccessResponse.newBuilder()
                    .setMessage("200")
                    .build();

            AccountCreationProto.CreateAccountResponse response = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setSuccess(success)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Сервер не отвечает")
                    .build();
            AccountCreationProto.CreateAccountResponse response = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
