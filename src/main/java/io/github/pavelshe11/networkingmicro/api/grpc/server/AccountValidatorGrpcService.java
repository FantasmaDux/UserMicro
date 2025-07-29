package io.github.pavelshe11.networkingmicro.api.grpc.server;

import io.github.pavelshe11.networking.grpc.AccountValidatorProto;
import io.github.pavelshe11.networking.grpc.AccountValidatorServiceGrpc;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class AccountValidatorGrpcService extends AccountValidatorServiceGrpc.AccountValidatorServiceImplBase {

    private final AccountRepository accountRepository;

    @Override
    public void checkEmail(AccountValidatorProto.CheckEmailRequest request,
                           StreamObserver<AccountValidatorProto.CheckEmailResponse> responseObserver) {

        var account = accountRepository.findByEmail(request.getEmail());
        boolean email_exists = account.isPresent();

        var response = AccountValidatorProto.CheckEmailResponse.newBuilder()
                .setExists(email_exists);

        if (email_exists && request.getReturnAccountId()) {
            response.setAccountId(account.get().getId().toString());
        }

        responseObserver.onNext(response.build()); // шлет сообщение
        responseObserver.onCompleted(); // сообщает, что сервер ответил на реквест и больше ответов не будет
    }
}
