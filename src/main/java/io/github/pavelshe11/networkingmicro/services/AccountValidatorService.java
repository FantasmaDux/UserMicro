package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.authmicro.grpc.AccountValidatorProto;
import io.github.pavelshe11.authmicro.grpc.AccountValidatorServiceGrpc;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AccountValidatorService extends AccountValidatorServiceGrpc.AccountValidatorServiceImplBase {

    private final AccountRepository accountRepository;

    @Override
    public void checkEmail(AccountValidatorProto.CheckEmailRequest request,
                           StreamObserver<AccountValidatorProto.CheckEmailResponse> responseObserver) {
        boolean email_exists = accountRepository.existsByEmail(request.getEmail());

        AccountValidatorProto.CheckEmailResponse response = AccountValidatorProto.CheckEmailResponse.newBuilder()
                .setExists(email_exists)
                .build();

        responseObserver.onNext(response); // шлет сообщение
        responseObserver.onCompleted(); // сообщает, что сервер ответил на реквест и больше ответов не будет
    }
}
