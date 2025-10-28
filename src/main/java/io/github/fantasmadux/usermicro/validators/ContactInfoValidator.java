package io.github.fantasmadux.usermicro.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.github.fantasmadux.usermicro.api.dto.FieldErrorDto;
import io.github.fantasmadux.usermicro.api.dto.requests.ContactInfoAddRequestDto;
import io.github.fantasmadux.usermicro.api.dto.requests.ContactInfoUpdateListRequestDto;
import io.github.fantasmadux.usermicro.normalization.DataNormalisation;
import io.github.fantasmadux.usermicro.store.entities.AccountContactInfoEntity;
import io.github.fantasmadux.usermicro.store.entities.AccountEntity;
import io.github.fantasmadux.usermicro.store.enums.ContactMethodType;
import io.github.fantasmadux.usermicro.store.repositories.AccountContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static io.github.fantasmadux.usermicro.constants.ValidationConstants.ACCEPTABLE_SYMBOLS_LINK_PATTERN;
import static io.github.fantasmadux.usermicro.constants.ValidationConstants.ACCEPTABLE_TLDS;

@Component
@RequiredArgsConstructor
public class ContactInfoValidator {

    private final CommonFieldsValidator commonFieldsValidator;
    private final DataNormalisation dataNormalisation;
    private final AccountContactInfoRepository accountContactInfoRepository;
    private static final Logger log = LoggerFactory.getLogger(ContactInfoValidator.class);

    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public Set<FieldErrorDto> validateContactInfoForAdd(AccountEntity account, ContactInfoAddRequestDto request) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();
        String rawContact = request.getContact();
        ContactMethodType methodType = request.getContactMethodType();

        if (rawContact == null || rawContact.isBlank()) {
            errors.add(commonFieldsValidator.createFieldErrorDto(request.getContact(), null, "field.empty"));
            return errors;
        }

        String normalizedContact = dataNormalisation.normalizeContact(request.getContact(), methodType);


        Optional<AccountContactInfoEntity> existing =
                accountContactInfoRepository.findByContactAndAccount(normalizedContact, account);

        if (existing.isPresent()) {
            UUID conflictingId = existing.get().getId();
            errors.add(commonFieldsValidator.createFieldErrorDto(
                    "contact",
                    null,
                    "error.contact.already.exists",
                    conflictingId
            ));
        }

        if (methodType == ContactMethodType.EMAIL) {
            validateEmailFieldForContactInfo(request.getContact(), errors, null);
        } else if (methodType == ContactMethodType.LINK) {
            validateLink(request.getContact(), errors, null);
        } else if (methodType == ContactMethodType.PHONE) {
            validatePhoneNumberField(request.getContact(), errors, null);
        } else {
            errors.add(commonFieldsValidator.createFieldErrorDto(rawContact, null, "method.unsupported.type"));
        }
        return errors;
    }

    public Set<FieldErrorDto> validateContactInfoForEdit(AccountEntity account, ContactInfoUpdateListRequestDto request) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();
        Set<String> seen = new HashSet<>();

        for (ContactInfoUpdateListRequestDto.ContactInfoUpdateRequestDto method : request.getAccountContactMethods()) {
            String contact = method.getContact();
            String trimmedContact = contact != null ? contact.trim().toLowerCase() : null;
            ContactMethodType newType = method.getContactMethodType();
            String methodType = method.getContactMethodType() != null ? method.getContactMethodType().toString() : "null";
            String key = methodType + "::" + (trimmedContact != null ? trimmedContact : "null");
            UUID contactId = method.getContactId();

            boolean contactChanged = true;

            Optional<AccountContactInfoEntity> existingContactOpt = accountContactInfoRepository
                    .findById(method.getContactId());

            if (existingContactOpt.isPresent()) {
                AccountContactInfoEntity existingContact = existingContactOpt.get();

                contactChanged = trimmedContact != null && !existingContact.getContact().equalsIgnoreCase(trimmedContact);
                boolean typeChanged = method.getContactMethodType() != null &&
                        !existingContact.getContactMethod().equals(method.getContactMethodType());
                boolean visibilityChanged = method.getVisibility() != null &&
                        !existingContact.getVisibility().equals(method.getVisibility());

                if (!contactChanged && !typeChanged && !visibilityChanged) {
                    continue;
                }

                if (typeChanged && !contactChanged) {
                    trimmedContact = existingContact.getContact().toLowerCase();
                }
            }

            if (newType != null && trimmedContact != null) {
                switch (newType) {
                    case EMAIL -> validateEmailFieldForContactInfo(trimmedContact, errors, contactId);
                    case LINK -> validateLink(trimmedContact, errors, contactId);
                    case PHONE -> validatePhoneNumberField(trimmedContact, errors, contactId);
                }
            }

            if (!seen.add(key)) {
                errors.add(commonFieldsValidator.createFieldErrorDto("contact", null, "error.contact.already.exists",
                        contactId));
                continue;
            }

            if (contactChanged && trimmedContact != null
                    && accountContactInfoRepository.existsByContactAndAccount(trimmedContact, account)) {
                errors.add(commonFieldsValidator.createFieldErrorDto("contact", null,
                        "error.contact.already.exists", contactId));
            }

        }

        return errors;
    }

    private void validateLink(String contact, Set<FieldErrorDto> errors, UUID objectId) {

        String[] schemes = {"https", "http"};
        long options = UrlValidator.NO_FRAGMENTS;

        UrlValidator validator = new UrlValidator(schemes, options);

        if (!validator.isValid(contact)) {
            objectIdResolver(errors, "contact", null, "link.not.accepted", objectId);
        }

        try {
            URI uri = new URI(contact);
            String host = uri.getHost();
            if (host == null || ACCEPTABLE_TLDS.stream().noneMatch(host::endsWith)) {
                log.error("uri не прошёл проверку");
                objectIdResolver(errors, "contact", null, "link.not.accepted", objectId);
            }
        } catch (URISyntaxException e) {
            log.error("Не принят uri");
            objectIdResolver(errors, "contact", null, "link.not.accepted", objectId);
        }

        if (!contact.matches(ACCEPTABLE_SYMBOLS_LINK_PATTERN)) {
            objectIdResolver(errors, "contact", null, "field.has.forbidden.symbols", objectId);
        }

        commonFieldsValidator.validateTooLongField(contact, contact, errors);
    }

    private void validatePhoneNumberField(String contact, Set<FieldErrorDto> errors, UUID objectId) {
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(contact, "RU");

            if (!phoneUtil.isValidNumber(phoneNumber)) {
                objectIdResolver(errors, "contact", null, "phone.not.valid", objectId);
            }
        } catch (NumberParseException e) {
            objectIdResolver(errors, "contact", null, "phone.parse.error", objectId);
        }
    }

    public void validateEmailFieldForContactInfo(String email, Set<FieldErrorDto> errors, UUID objectId) {

        if (email == null || email.isBlank()) {
            objectIdResolver(errors, "contact", null, "field.empty", objectId);
            return;
        }

        EmailValidator validator = EmailValidator.getInstance(false, true);

        if (!validator.isValid(email)) {
            objectIdResolver(errors, "contact", null, "email.format.incorrect", objectId);
        }
    }

    private void objectIdResolver(Set<FieldErrorDto> errors, String field, Object[] args, String code, UUID objectId) {
        if (objectId != null) {
            errors.add(commonFieldsValidator.createFieldErrorDto(field, args, code, objectId));
        } else {
            errors.add(commonFieldsValidator.createFieldErrorDto(field, args, code));
        }
    }
}
