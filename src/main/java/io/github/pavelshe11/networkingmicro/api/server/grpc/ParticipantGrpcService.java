package io.github.pavelshe11.networkingmicro.api.server.grpc;

import io.github.pavelshe11.networkingmicro.grpc.ParticipantServiceGrpc;
import io.github.pavelshe11.networkingmicro.grpc.findByRefIdProto;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.InitiativeRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class ParticipantGrpcService extends ParticipantServiceGrpc.ParticipantServiceImplBase {
    private final AccountRepository accountRepository;
    private final InitiativeRepository initiativeRepository;

    @Override
    public void findByRefId(findByRefIdProto.existsByRefIdRequest request, StreamObserver<findByRefIdProto.existsByRefIdResponse> responseObserver) {
        boolean existing = accountRepository.existsById(UUID.fromString(request.getRefId())) ||
                initiativeRepository.existsById(UUID.fromString(request.getRefId()));

        findByRefIdProto.existsByRefIdResponse response = findByRefIdProto.existsByRefIdResponse.newBuilder()
                .setExistingEntity(existing)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
