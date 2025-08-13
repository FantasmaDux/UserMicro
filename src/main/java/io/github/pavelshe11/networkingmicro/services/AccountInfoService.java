package io.github.pavelshe11.networkingmicro.services;

import com.google.protobuf.Value;
import io.github.pavelshe11.networkingmicro.grpc.getAccountInfoProto;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInfoService {

    private final AccountRepository accountRepository;

    public getAccountInfoProto.GetAccountInfoResponse.Builder getAccountInfoByEmail(String email) {

        Optional<AccountEntity> accountOpt = accountRepository.findByEmail(email);

        return getBuilderResponse(accountOpt);
    }

    public getAccountInfoProto.GetAccountInfoResponse.Builder getAccountById(String accountId) {

        Optional<AccountEntity> accountOpt = accountRepository.findById(UUID.fromString(accountId));

        return getBuilderResponse(accountOpt);
    }

    private getAccountInfoProto.GetAccountInfoResponse.Builder getBuilderResponse(Optional<AccountEntity> accountOpt) {
        getAccountInfoProto.GetAccountInfoResponse.Builder responseBuild = getAccountInfoProto.GetAccountInfoResponse.newBuilder();

        if (accountOpt.isEmpty()) {
            return responseBuild;
        }

        AccountEntity account = accountOpt.get();
        Map<String, Value> userData = new HashMap<>();

        userData.put("account_id", Value.newBuilder().setStringValue(account.getId().toString()).build());
        userData.put("role", Value.newBuilder().setStringValue(account.isAdmin() ? "admin" : "user").build());
        userData.put("ip", Value.newBuilder().setStringValue(Optional.ofNullable(account.getIp()).orElse("")).build());

        responseBuild.putAllUserData(userData);

        return responseBuild;
    }

}
