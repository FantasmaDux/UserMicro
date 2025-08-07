package io.github.pavelshe11.networkingmicro.api.grpc.server;

import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.AccountCreationServiceGrpc;
import io.github.pavelshe11.networkingmicro.services.AccountCreationService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AccountCreationGrpcService extends AccountCreationServiceGrpc.AccountCreationServiceImplBase {
    private final AccountCreationService accountCreationService;

    @Override
    public void createAccount(AccountCreationProto.CreateAccountRequest request, StreamObserver<AccountCreationProto.CreateAccountResponse> responseObserver) {

        AccountCreationProto.CreateAccountResponse accountResponse =
                accountCreationService.createAccount(request.getUserDataMap());

        responseObserver.onNext(accountResponse);
        responseObserver.onCompleted();

    }
}
