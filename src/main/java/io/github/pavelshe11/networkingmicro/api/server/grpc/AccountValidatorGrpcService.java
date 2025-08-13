package io.github.pavelshe11.networkingmicro.api.server.grpc;

import com.google.protobuf.Value;
import io.github.pavelshe11.networking.grpc.AccountValidatorProto;
import io.github.pavelshe11.networking.grpc.AccountValidatorServiceGrpc;
import io.github.pavelshe11.networkingmicro.services.AccountValidatorService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;

@GrpcService
@RequiredArgsConstructor
public class AccountValidatorGrpcService extends AccountValidatorServiceGrpc.AccountValidatorServiceImplBase {

    private final AccountValidatorService accountValidatorService;

    @Override
    public void validateUserData(AccountValidatorProto.ValidateUserDataRequest request, StreamObserver<AccountValidatorProto.ValidateUserDataResponse> responseObserver) {

        Map<String, Value> userData = request.getUserDataMap();

        AccountValidatorProto.ValidateUserDataResponse response =
                accountValidatorService.validateUserData(userData);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
