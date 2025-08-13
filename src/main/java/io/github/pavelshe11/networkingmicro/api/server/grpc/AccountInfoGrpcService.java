package io.github.pavelshe11.networkingmicro.api.server.grpc;

import io.github.pavelshe11.networkingmicro.grpc.GetAccountInfoServiceGrpc;
import io.github.pavelshe11.networkingmicro.grpc.getAccountInfoProto;
import io.github.pavelshe11.networkingmicro.services.AccountInfoService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
@RequiredArgsConstructor
public class AccountInfoGrpcService extends GetAccountInfoServiceGrpc.GetAccountInfoServiceImplBase {
    private final AccountInfoService accountInfoService;

    @Override
    public void getAccountInfoByEmail(getAccountInfoProto.GetAccountInfoByEmailRequest request, StreamObserver<getAccountInfoProto.GetAccountInfoResponse> responseObserver) {

        getAccountInfoProto.GetAccountInfoResponse.Builder responseBuild =
                accountInfoService.getAccountInfoByEmail(request.getEmail());


        responseObserver.onNext(responseBuild.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAccountById(getAccountInfoProto.GetAccountByIdRequest request, StreamObserver<getAccountInfoProto.GetAccountInfoResponse> responseObserver) {

        getAccountInfoProto.GetAccountInfoResponse.Builder responseBuild =
                accountInfoService.getAccountById(request.getAccountId());

        responseObserver.onNext(responseBuild.build());
        responseObserver.onCompleted();
    }
}
