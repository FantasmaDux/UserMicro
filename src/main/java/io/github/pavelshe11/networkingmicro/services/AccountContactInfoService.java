package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.AccountContactInfoDto;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoDeleteRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateRequestDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ContactsLimitException;
import io.github.pavelshe11.networkingmicro.api.exceptions.FieldValidationException;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.normalization.DataNormalisation;
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
    private final DataNormalisation dataNormalisation;

    public void updateContactInfo(UUID accountId, ContactInfoUpdateRequestDto request) {
        log.info("Начало добавления контактной информации: {}, данные: {}", accountId, request);
        Set<FieldErrorDto> validationErrors = accountDataValidatior.validateContactInfo(request);

        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }

        List<AccountContactInfoDto> limitedContacts = request.getAccountContactMethods()
                .stream()
                .limit(4)
                .toList();

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();
        List<AccountContactInfoEntity> existingContacts = new ArrayList<>(account.getAccountContactInfos());

        for (AccountContactInfoDto method : limitedContacts) {
            String normalizedContact
                    = dataNormalisation.normalizeContact(method.getContact(), method.getContactMethodType());

            Optional<AccountContactInfoEntity> existingContactOpt = existingContacts.stream()
                    .filter(c -> {
                        String existingNormalized = dataNormalisation.normalizeContact(c.getContact(), c.getContactMethod());
                        return existingNormalized != null && existingNormalized.equalsIgnoreCase(normalizedContact);
                    })
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
        String normalizeContact
                = dataNormalisation.normalizeContact(method.getContact(), method.getContactMethodType());

        if (account.getAccountContactInfos().size() >= 5) {
            log.error("Попытка сохранить больше 5 контактов.");
            throw new ContactsLimitException();
        }

        boolean contactTakenByOther = accountContactInfoRepository.existsByContactIgnoreCase(normalizeContact) &&
                account.getAccountContactInfos().stream()
                        .noneMatch(info -> info.getContact().equalsIgnoreCase(normalizeContact));

        if (contactTakenByOther) {
            log.error("Контакт '{}' уже используется.", method.getContact());
            throw new ServerAnswerException();
        }

        String faviconUrl = null;
        if (method.getContactMethodType() == ContactMethodType.LINK ||
                method.getContactMethodType() == ContactMethodType.EMAIL) {
            faviconUrl = fetchFaviconUrl(normalizeContact);
        }

        AccountContactInfoEntity newContact = AccountContactInfoEntity.builder()
                .contact(normalizeContact)
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

    public void editContactInfoById(UUID accountId, UUID contactId, AccountContactInfoDto request) {
        log.info("Начало обновления контактной информации: {}, данные: {}", accountId, request);

        ContactInfoUpdateRequestDto tempRequest = new ContactInfoUpdateRequestDto(List.of(request));
        Set<FieldErrorDto> validationErrors = accountDataValidatior.validateContactInfo(tempRequest);

        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }

        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        AccountEntity account = accountOpt.get();

        Optional<AccountContactInfoEntity> contactOpt = accountContactInfoRepository.findById(contactId);
        if (contactOpt.isEmpty()) {
            log.error("Контакта не существует.");
            throw new ServerAnswerException();
        }

        AccountContactInfoEntity contact = contactOpt.get();

        if (account.getMainEmailContact().getId().equals(contactId)) {
            log.warn("Попытка изменить основной email, что запрещено.");
            throw new FieldValidationException("handle.error", List.of(
                    new FieldErrorDto("contactId", "main.email.edit.forbidden")
            ));
        }

        String faviconUrl = null;
        if (request.getContactMethodType() == ContactMethodType.LINK ||
                request.getContactMethodType() == ContactMethodType.EMAIL) {
            faviconUrl = fetchFaviconUrl(request.getContact());
        }

        String normalizeContact
                = dataNormalisation.normalizeContact(request.getContact(), request.getContactMethodType());

        contact.setAccount(account);
        contact.setContact(normalizeContact);
        contact.setContactMethod(request.getContactMethodType());
        contact.setIconUrl(faviconUrl);
        contact.setVisibility(request.isVisibility());

        accountContactInfoRepository.save(contact);
    }
}
