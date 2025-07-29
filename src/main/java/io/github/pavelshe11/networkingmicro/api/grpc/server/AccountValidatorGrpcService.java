package io.github.pavelshe11.networkingmicro.api.grpc.server;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountValidatorProto;
import io.github.pavelshe11.networking.grpc.AccountValidatorServiceGrpc;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;

@GrpcService
@RequiredArgsConstructor
public class AccountValidatorGrpcService extends AccountValidatorServiceGrpc.AccountValidatorServiceImplBase {

    private final AccountDataValidation accountDataValidator;

    @Override
    public void validateUserData(AccountValidatorProto.ValidateUserDataRequest request, StreamObserver<AccountValidatorProto.ValidateUserDataResponse> responseObserver) {

        Map<String, Value> userData = request.getUserDataMap();

        List<ErrorProto.FieldError> errors = new ArrayList<>();

        errors.addAll(accountDataValidator.validateEmail(userData));
        errors.addAll(accountDataValidator.validatePolitics(userData));
        errors.addAll(accountDataValidator.validateDomenName(userData));
//        errors.addAll(accountDataValidator.validateAccountExisting(userData));

        boolean isAccountValid = errors.isEmpty();
        if (isAccountValid) {
            Optional<UUID> accountId = accountDataValidator.getAccountIdIfExists(userData);
        }

        AccountValidatorProto.ValidateUserDataResponse.Builder response =
                AccountValidatorProto.ValidateUserDataResponse.newBuilder()
                        .setAccept(isAccountValid)
                        .setError(isAccountValid ? ""
                                : "Валидация пользовательских данных не пройдена.")
                        .addAllDetailedErrors(errors);

        if (isAccountValid) {
            accountDataValidator.getAccountIdIfExists(userData)
                    .map(accountId -> accountId.toString())
                    .ifPresent(accountId -> response.setAccountId(accountId));
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
