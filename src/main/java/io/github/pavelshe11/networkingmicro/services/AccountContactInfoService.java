package io.github.pavelshe11.networkingmicro.services;

import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoAddRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoDeleteRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateListRequestDto;
import io.github.pavelshe11.networkingmicro.api.exceptions.ContactsLimitException;
import io.github.pavelshe11.networkingmicro.api.exceptions.FieldValidationException;
import io.github.pavelshe11.networkingmicro.api.exceptions.ServerAnswerException;
import io.github.pavelshe11.networkingmicro.normalization.DataNormalisation;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.util.HelperUtils;
import io.github.pavelshe11.networkingmicro.validators.ContactInfoValidator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AccountContactInfoService {
    private static final Logger log = LoggerFactory.getLogger(AccountContactInfoService.class);
    private final AccountRepository accountRepository;
    private final AccountContactInfoRepository accountContactInfoRepository;
    private final DataNormalisation dataNormalisation;
    private static final int MAX_CONTACT_FOR_ACCOUNT_LIMIT = 5;
    private final ContactInfoValidator contactInfoValidator;

    public void addContactInfo(UUID accountId, ContactInfoAddRequestDto request) {
        log.info("Начало добавления контактной информации: {}, данные: {}", accountId, request);

        AccountEntity account = getAccountOrThrow(accountId);

        Set<FieldErrorDto> validationErrors = contactInfoValidator.validateContactInfoForAdd(account, request);

        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }

        List<AccountContactInfoEntity> existingContacts = new ArrayList<>(account.getAccountContactInfos());

        String normalizedContact
                = dataNormalisation.normalizeContact(request.getContact(), request.getContactMethodType());

        Optional<AccountContactInfoEntity> existingContactOpt = existingContacts.stream()
                .filter(c -> {
                    String existingNormalized = dataNormalisation.normalizeContact(c.getContact(),
                            c.getContactMethod());
                    return existingNormalized != null && existingNormalized.equalsIgnoreCase(normalizedContact);
                })
                .findFirst();

        if (existingContactOpt.isPresent()) {
            handleExistingContact(account, existingContactOpt.get(), request);
        } else {
            handleNewContact(account, request);
        }
        accountRepository.save(account);
    }

    private void handleNewContact(AccountEntity account, ContactInfoAddRequestDto request) {
        String normalizeContact
                = dataNormalisation.normalizeContact(request.getContact(), request.getContactMethodType());

        if (account.getAccountContactInfos().size() >= MAX_CONTACT_FOR_ACCOUNT_LIMIT) {
            log.error("Попытка сохранить больше 5 контактов.");
            throw new ContactsLimitException();
        }

        String faviconUrl = null;
        if (request.getContactMethodType() == ContactMethodType.LINK ||
                request.getContactMethodType() == ContactMethodType.EMAIL) {
            faviconUrl = HelperUtils.fetchFaviconUrl(normalizeContact);
        }

        boolean isModifiable = !(account.getMainEmailContact() != null
                && account.getMainEmailContact().getContact().equalsIgnoreCase(request.getContact()));

        AccountContactInfoEntity newContact = AccountContactInfoEntity.builder()
                .contact(normalizeContact)
                .contactMethod(request.getContactMethodType())
                .iconUrl(faviconUrl)
                .visibility(request.getVisibility())
                .account(account)
                .modifiable(isModifiable)
                .build();

        account.getAccountContactInfos().add(newContact);
    }

    private void handleExistingContact(AccountEntity account, AccountContactInfoEntity existingContact,
                                       ContactInfoAddRequestDto request) {
        log.info("Обновление существующего контакта: {}", existingContact.getContact());

        account.getAccountContactInfos().remove(existingContact);

        handleNewContact(account, request);
    }

    public void deleteContactInfoMethod(UUID accountId, ContactInfoDeleteRequestDto request) {
        AccountEntity account = getAccountOrThrow(accountId);

        for (UUID contactId : request.getContactMethodsIds()) {
            Optional<AccountContactInfoEntity> accountContactInfoOpt =
                    accountContactInfoRepository.findById(contactId);

            if (accountContactInfoOpt.isEmpty()) {
                throw new FieldValidationException("handle.error", List.of(
                        new FieldErrorDto("contactId", "contact.not.found", contactId)));
            }

            AccountContactInfoEntity contact = accountContactInfoOpt.get();

            if (!contact.getAccount().getId().equals(accountId)) {
                log.error("Контакт {} не принадлежит аккаунту {}", contactId, accountId);
                throw new FieldValidationException("handle.error", List.of(
                        new FieldErrorDto("contactId", "contact.not.found", contactId)));
            }

            if (!contact.isModifiable()) {
                log.warn("Попытка удалить не изменяемую основную почту");
                throw new FieldValidationException("handle.error", List.of(
                        new FieldErrorDto("contactId", "main.email.edit.forbidden", contactId)));
            }


            accountContactInfoRepository.delete(contact);
            account.getAccountContactInfos().remove(contact);

        }
        accountRepository.save(account);
    }

    public void editContactInfoById(UUID accountId, ContactInfoUpdateListRequestDto request) {
        log.info("Начало обновления контактной информации: {}, данные: {}", accountId, request);

        AccountEntity account = getAccountOrThrow(accountId);

        Set<FieldErrorDto> validationErrors = contactInfoValidator.validateContactInfoForEdit(account, request);
        if (!validationErrors.isEmpty()) {
            log.error("Ошибка валидации данных: {}", validationErrors);
            throw new FieldValidationException("validation.error", validationErrors.stream().toList());
        }


        for (ContactInfoUpdateListRequestDto.ContactInfoUpdateRequestDto newContact : request.getAccountContactMethods()) {

            Optional<AccountContactInfoEntity> existingContactOpt = accountContactInfoRepository
                    .findById(newContact.getContactId());

            if (existingContactOpt.isEmpty()) {
                log.error("Контакт с ID {} не найден", newContact.getContactId());
                throw new FieldValidationException("handle.error", List.of(
                        new FieldErrorDto("contactId", "contact.not.found", newContact.getContactId())
                ));
            }

            AccountContactInfoEntity contact = existingContactOpt.get();

            editFromOldToNewContact(account, newContact, contact);

            accountContactInfoRepository.save(contact);

        }
    }

    private void editFromOldToNewContact(AccountEntity account,
                                         ContactInfoUpdateListRequestDto.ContactInfoUpdateRequestDto newContact,
                                         AccountContactInfoEntity contact) {
        boolean isMainEmail = account.getMainEmailContact() != null
                && account.getMainEmailContact().getId().equals(newContact.getContactId());

        if (isMainEmail) {
            boolean contactChanged = newContact.getContact() != null
                    && !Objects.equals(newContact.getContact(), contact.getContact());

            boolean typeChanged = newContact.getContactMethodType() != null
                    && !Objects.equals(newContact.getContactMethodType(), contact.getContactMethod());

            if (contactChanged || typeChanged) {
                log.error("Попытка изменить основной email, что запрещено.");
                throw new FieldValidationException("handle.error", List.of(
                        new FieldErrorDto("contactId", "main.email.edit.forbidden")
                ));
            }

        } else {
            if (newContact.getContact() != null) {
                contact.setContact(newContact.getContact());
            }

            if (newContact.getContactMethodType() != null) {
                contact.setContactMethod(newContact.getContactMethodType());
            }

            if ((newContact.getContactMethodType() == ContactMethodType.LINK
                    || newContact.getContactMethodType() == ContactMethodType.EMAIL)
                    && newContact.getContact() != null) {
                String faviconUrl = HelperUtils.fetchFaviconUrl(newContact.getContact());
                contact.setIconUrl(faviconUrl);
            }
        }

        if (newContact.getVisibility() != null) {
            contact.setVisibility(newContact.getVisibility());
        }
    }

    private AccountEntity getAccountOrThrow(UUID accountId) {
        Optional<AccountEntity> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            log.error("Аккаунта не существует.");
            throw new ServerAnswerException();
        }

        return accountOpt.get();
    }
}
