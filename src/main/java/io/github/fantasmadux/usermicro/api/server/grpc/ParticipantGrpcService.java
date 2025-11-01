package io.github.fantasmadux.usermicro.api.server.grpc;

import io.github.fantasmadux.usermicro.grpc.ParticipantServiceGrpc;
import io.github.fantasmadux.usermicro.grpc.findByRefIdAndParticipantTypeProto;
import io.github.fantasmadux.usermicro.store.repositories.AccountRepository;
import io.github.fantasmadux.usermicro.store.repositories.InitiativeRepository;
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
