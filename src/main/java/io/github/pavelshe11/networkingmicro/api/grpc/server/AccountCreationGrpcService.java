package io.github.pavelshe11.networkingmicro.api.grpc.server;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.AccountCreationServiceGrpc;
import io.github.pavelshe11.networking.grpc.ErrorProto;
import io.github.pavelshe11.networkingmicro.services.AccountCreationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Map;

@GrpcService
@RequiredArgsConstructor
public class AccountCreationGrpcService extends AccountCreationServiceGrpc.AccountCreationServiceImplBase {
    private final AccountCreationService accountCreationService;

    @Override
    public void createAccount(AccountCreationProto.CreateAccountRequest request, StreamObserver<AccountCreationProto.CreateAccountResponse> responseObserver) {

        Map<String, Value> userData = request.getUserDataMap();
        List<ErrorProto.FieldError> errors = accountCreationService.validate(userData);
        AccountCreationProto.CreateAccountResponse accountResponse;

        if (!errors.isEmpty()) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Ошибка валидации данных")
                    .addAllDetailedErrors(errors)
                    .build();

            accountResponse = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();
        } else {
            accountResponse = accountCreationService.createAccount(userData);
        }

        responseObserver.onNext(accountResponse);
        responseObserver.onCompleted();

    }
}
