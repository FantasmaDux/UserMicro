package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.AccountContactInfoDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoDeleteRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ContactAlreadyExistsException;
import io.github.pavelshe11.networkingmicro.api.exceptions.FieldValidationException;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.validators.AccountDataValidation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;

@Service
@AllArgsConstructor
public class AccountContactInfoService {
    private static final Logger log = LoggerFactory.getLogger(AccountContactInfoService.class);
    private final AccountDataValidation accountDataValidatior;
    private final AccountRepository accountRepository;
    private final AccountContactInfoRepository accountContactInfoRepository;

    public void updateContactInfo(UUID accountId, ContactInfoUpdateRequestDto request) {
        log.info("Начало обновления контактной информации: {}, данные: {}", accountId, request);
        Set<FieldErrorDto> validationErrors = accountDataValidatior.validateContactInfo(request);

        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }

        List<AccountContactInfoDto> limitedContacts = request.getAccountContactMethods()
                .stream()
                .limit(5)
                .toList();

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();
        List<AccountContactInfoEntity> existingContacts = new ArrayList<>(account.getAccountContactInfos());

        for (AccountContactInfoDto method : limitedContacts) {
            String normalizedContact = method.getContact().trim().toLowerCase();

            Optional<AccountContactInfoEntity> existingContactOpt = existingContacts.stream()
                    .filter(c -> c.getContact().trim().equalsIgnoreCase(normalizedContact))
                    .findFirst();

            if (existingContactOpt.isPresent()) {
                handleExistingContact(account, existingContactOpt.get(), method);
            } else {
                handleNewContact(account, method);
            }
        }
        accountRepository.save(account);
    }

    private void handleNewContact(AccountEntity account, AccountContactInfoDto method) {
        String contact = method.getContact().trim().toLowerCase();

        boolean contactTakenByOther = accountContactInfoRepository.existsByContactIgnoreCase(contact) &&
                account.getAccountContactInfos().stream()
                        .noneMatch(info -> info.getContact().equalsIgnoreCase(contact));

        if (contactTakenByOther) {
            log.error("Контакт '{}' уже используется.", method.getContact());
            throw new ContactAlreadyExistsException();
        }

        String faviconUrl = null;
        if (method.getContactMethodType() == ContactMethodType.LINK ||
                method.getContactMethodType() == ContactMethodType.EMAIL) {
            faviconUrl = fetchFaviconUrl(method.getContact());
        }

        AccountContactInfoEntity newContact = AccountContactInfoEntity.builder()
                .contact(method.getContact())
                .contactMethod(method.getContactMethodType())
                .iconUrl(faviconUrl)
                .account(account)
                .build();

        account.getAccountContactInfos().add(newContact);
    }

    private void handleExistingContact(AccountEntity account, AccountContactInfoEntity existingContact, AccountContactInfoDto method) {
        log.info("Обновление существующего контакта: {}", existingContact.getContact());

        account.getAccountContactInfos().remove(existingContact);

        handleNewContact(account, method);
    }


    private String fetchFaviconUrl(String contact) {
        String faviconUrl = "";

        try {
            String domain;

            if (contact.contains("@")) {
                domain = contact.substring(contact.indexOf("@") + 1);
            } else {
                URL url = new URL(contact);
                domain = url.getHost();
            }

            if (!domain.isEmpty()) {
                faviconUrl = "https://" + domain + "/favicon.ico";
            }

        } catch (Exception e) {
            log.error("Ошибка извлечения favicon.");
        }

        return faviconUrl;
    }

    public void deleteContactInfoMethod(UUID accountId, ContactInfoDeleteRequestDto request) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        for ( UUID contactId : request.getContactMethodsIds()) {
            Optional<AccountContactInfoEntity> accountContactInfoOpt =
                    accountContactInfoRepository.findById(contactId);
            if (accountContactInfoOpt.isPresent()) {
                AccountContactInfoEntity info = accountContactInfoOpt.get();
                if (info.getAccount().getId().equals(accountId)) {
                    accountContactInfoRepository.delete(info);
                    account.getAccountContactInfos().remove(info);
                } else {
                    log.error("Контакт {} не принадлежит аккаунту {}", contactId, accountId);
                }
            }
        }
        accountRepository.save(account);
    }

}
