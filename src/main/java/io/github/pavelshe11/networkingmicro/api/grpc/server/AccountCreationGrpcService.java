package io.github.pavelshe11.networkingmicro.api.grpc.server;

import io.github.pavelshe11.networking.grpc.AccountCreationProto;
import io.github.pavelshe11.networking.grpc.AccountCreationServiceGrpc;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDate;

@GrpcService
@RequiredArgsConstructor
public class AccountCreationGrpcService extends AccountCreationServiceGrpc.AccountCreationServiceImplBase {

    private final AccountRepository accountRepository;
    private final EducationalInstitutionRepository educationalInstitutionRepository;


    @Override
    public void createAccount(AccountCreationProto.CreateAccountRequest request, StreamObserver<AccountCreationProto.CreateAccountResponse> responseObserver) {

        try {
            String email = request.getEmail();
            String domenNameOfEmail = email.substring(email.indexOf("@") +1);

            var educationalInstitution = educationalInstitutionRepository.findByDomenName(domenNameOfEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Домен не найден: " + domenNameOfEmail));

            AccountEntity account = new AccountEntity();
            account.setEmail(request.getEmail());
            account.setCity(null);
            account.setSpecialization(null);
            account.setEducationalInstitution(educationalInstitution);
            account.setSetSkills(null);
            account.setAvatar(null);
            account.setNickname("Test");
            account.setFirstName("Test");
            account.setLastName("Test");
            account.setMiddleName("Test");
            account.setProfessor(false);
            account.setAdmin(false);
            account.setVisible(false);
            account.setConsulting(false);
            account.setDateOfBirth(LocalDate.now());
            account.setCourseNumber((short) 0);

            accountRepository.save(account);

            AccountCreationProto.SuccessResponse success
                     = AccountCreationProto.SuccessResponse.newBuilder()
                    .setMessage("200")
                    .build();

            AccountCreationProto.CreateAccountResponse response = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setSuccess(success)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            AccountCreationProto.ErrorResponse error = AccountCreationProto.ErrorResponse.newBuilder()
                    .setError("Сервер не отвечает")
                    .build();
            AccountCreationProto.CreateAccountResponse response = AccountCreationProto.CreateAccountResponse.newBuilder()
                    .setError(error)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
