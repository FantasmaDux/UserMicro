package io.github.pavelshe11.networkingmicro.api.grpc.server;

import io.github.pavelshe11.networking.grpc.CheckIsDomainExistsProto;
import io.github.pavelshe11.networking.grpc.CheckIsDomainExistsServiceGrpc;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class CheckIsDomainExistsGrpcService extends CheckIsDomainExistsServiceGrpc.CheckIsDomainExistsServiceImplBase {

    private final EducationalInstitutionRepository educationalInstitutionRepository;

    @Override
    public void checkIsDomainExists(CheckIsDomainExistsProto.CheckInstitutionDomainRequest request, StreamObserver<CheckIsDomainExistsProto.CheckInstitutionDomainResponse> responseObserver) {
        boolean exists = educationalInstitutionRepository
                .findByDomenName(request.getDomain())
                .isPresent();

        var response = CheckIsDomainExistsProto.CheckInstitutionDomainResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

}
