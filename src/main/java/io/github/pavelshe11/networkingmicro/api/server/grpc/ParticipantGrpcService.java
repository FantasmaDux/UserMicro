package io.github.pavelshe11.networkingmicro.api.server.grpc;

import io.github.pavelshe11.networkingmicro.grpc.ParticipantServiceGrpc;
import io.github.pavelshe11.networkingmicro.grpc.findByRefIdAndParticipantTypeProto;
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
    public void findByRefIdAndParticipantType(findByRefIdAndParticipantTypeProto.existsByRefIdAndParticipantTypeRequest request, StreamObserver<findByRefIdAndParticipantTypeProto.existsByRefIdAndParticipantTypeResponse> responseObserver) {

        String participantType = request.getParticipantType().toLowerCase();

        boolean existingInAccounts = accountRepository.existsById(UUID.fromString(request.getRefId()))
                && participantType.equals("account");

        boolean existingInInitiatives = initiativeRepository.existsById(UUID.fromString(request.getRefId()))
                && participantType.equals("initiative");

        boolean existing = existingInAccounts || existingInInitiatives;

        findByRefIdAndParticipantTypeProto.existsByRefIdAndParticipantTypeResponse response =
                findByRefIdAndParticipantTypeProto.existsByRefIdAndParticipantTypeResponse.newBuilder()
                        .setExistingEntity(existing)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
