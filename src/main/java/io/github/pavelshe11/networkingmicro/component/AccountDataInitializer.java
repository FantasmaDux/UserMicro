package io.github.pavelshe11.networkingmicro.component;

import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountDataInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) {
        AccountEntity account = AccountEntity.builder()
                .email("admin@communicator.ru")
                .admin(true)
                .visible(true)
                .ip("1.1.1.1")
                .acceptedPrivacyPolicy(true)
                .acceptedPersonalDataProcessing(true)
                .build();

        accountRepository.save(account);
    }

}
