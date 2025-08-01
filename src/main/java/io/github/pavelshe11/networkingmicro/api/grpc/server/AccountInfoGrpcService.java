package io.github.pavelshe11.networkingmicro.api.grpc.server;

import io.github.pavelshe11.networkingmicro.grpc.GetAccountInfoServiceGrpc;
import io.github.pavelshe11.networkingmicro.grpc.getAccountInfoProto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountInfoGrpcService extends GetAccountInfoServiceGrpc.GetAccountInfoServiceImplBase {
    AccountRepository accountRepository;

    @Override
    public void getAccountInfo(getAccountInfoProto.GetAccountInfoRequest request, StreamObserver<getAccountInfoProto.GetAccountInfoResponse> responseObserver) {

        String email = request.getEmail();

        Optional<AccountEntity> accountOpt = accountRepository.findByEmail(email);

        getAccountInfoProto.GetAccountInfoResponse.Builder responseBuild = getAccountInfoProto.GetAccountInfoResponse.newBuilder();

        if (accountOpt.isPresent()) {
            AccountEntity account = accountOpt.get();

            if (account.getId() != null) {
                responseBuild.setAccountId(account.getId().toString());
            }

            if (account.isAdmin()) {
                responseBuild.setRole("admin");
            } else {
                responseBuild.setRole("user");
            }
        }

        responseObserver.onNext(responseBuild.build());
        responseObserver.onCompleted();
    }
}
