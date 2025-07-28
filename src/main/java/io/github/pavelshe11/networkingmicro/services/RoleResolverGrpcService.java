package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networking.grpc.CheckIsAdminProto;
import io.github.pavelshe11.networking.grpc.CheckIsAdminServiceGrpc;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class RoleResolverGrpcService extends CheckIsAdminServiceGrpc.CheckIsAdminServiceImplBase {
    private final AccountRepository accountRepository;

    @Override
    public void checkIsAdmin(CheckIsAdminProto.CheckIsAdminRequest request, StreamObserver<CheckIsAdminProto.CheckIsAdminResponse> responseObserver) {

        UUID accountId = UUID.fromString(request.getAccountId());

        var accountOpt = accountRepository.findById(accountId);

        boolean isAdmin = accountOpt.map(AccountEntity::isAdmin).orElse(false);

        CheckIsAdminProto.CheckIsAdminResponse response =
                CheckIsAdminProto.CheckIsAdminResponse.newBuilder()
                        .setIsAdmin(isAdmin)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
