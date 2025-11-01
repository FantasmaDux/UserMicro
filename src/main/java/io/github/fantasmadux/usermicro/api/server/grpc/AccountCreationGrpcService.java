package io.github.fantasmadux.usermicro.api.server.grpc;

import io.github.fantasmadux.usermicro.grpc.AccountCreationProto;
import io.github.fantasmadux.usermicro.grpc.AccountCreationServiceGrpc;
import io.github.fantasmadux.usermicro.services.AccountCreationService;
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
