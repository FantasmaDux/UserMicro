package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.grpc.getAccountInfoProto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInfoService {

    private final AccountRepository accountRepository;

    public getAccountInfoProto.GetAccountInfoResponse.Builder getAccountInfo(String email) {

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
        return responseBuild;
    }

    public getAccountInfoProto.CheckAccountByIdResponse.Builder checkAccountById(String accountId) {

        Optional<AccountEntity> accountOpt = accountRepository.findById(UUID.fromString(accountId));

        getAccountInfoProto.CheckAccountByIdResponse.Builder responseBuild
                = getAccountInfoProto.CheckAccountByIdResponse.newBuilder();

        if (accountOpt.isPresent()) {
            responseBuild.setAccept(true);
        }
        return responseBuild;
    }

}
