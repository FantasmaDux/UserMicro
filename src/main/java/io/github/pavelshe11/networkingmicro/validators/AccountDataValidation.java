package io.github.pavelshe11.networkingmicro.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.github.pavelshe11.networkingmicro.api.dto.FieldErrorDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoAddRequestDto;
import io.github.pavelshe11.networkingmicro.api.dto.requests.ContactInfoUpdateListRequestDto;
import io.github.pavelshe11.networkingmicro.normalization.DataNormalisation;
import io.github.pavelshe11.networkingmicro.store.entities.AccountContactInfoEntity;
import io.github.pavelshe11.networkingmicro.store.entities.AccountEntity;
import io.github.pavelshe11.networkingmicro.store.enums.ContactMethodType;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountContactInfoRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.AccountRepository;
import io.github.pavelshe11.networkingmicro.store.repositories.EducationalInstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountDataValidation {
    private final EducationalInstitutionRepository educationalInstitutionRepository;
    private final MessageSource messageSource;
    private final AccountRepository accountRepository;
    private final AccountContactInfoRepository accountContactInfoRepository;
    private final DataNormalisation dataNormalisation;
    private static final Logger log = LoggerFactory.getLogger(AccountDataValidation.class);

    private static final int FIELD_MAX_LENGTH = 32;
    private static final String ACCEPTABLE_SYMBOLS_PATTERN = "^[a-zA-Zа-яА-ЯёЁ]+$";
    private static final String ACCEPTABLE_SYMBOLS_LINK_PATTERN = "^[a-zA-Z0-9.:_/?=&%#-]+$";
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    private static final Set<String> ACCEPTABLE_TLDS = Set.of(".com", ".org", ".net", ".ru");
    private static final Set<String> REQUIRED_FIELDS = Set.of(
            "firstName", "email", "isProfessor", "isAdmin", "isVisible", "isConsulting", "lastName"
    );

    private boolean isRequired(String fieldName) {
        return REQUIRED_FIELDS.contains(fieldName);
    }

    public Set<FieldErrorDto> validateRegistrationData(Map<String, Object> userData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        validatePolitics(userData, errors);
        String emailToValidate = null;
        Object emailObj = userData.get("email");
        if (emailObj instanceof String emailStr) {
            emailToValidate = emailStr;
        }
        validateEmailField(emailToValidate, errors);

        validateFirstName(userData, errors);
        validateLastName(userData, errors);

        return errors;
    }

    public Set<FieldErrorDto> validateUpdateData(Map<String, Object> updatedData) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();

        if (updatedData.containsKey("firstName")) {
            validateTextField("firstName", updatedData, errors);
        }
        if (updatedData.containsKey("middleName")) {
            validateTextField("middleName", updatedData, errors);
        }
        if (updatedData.containsKey("lastName")) {
            validateTextField("lastName", updatedData, errors);
        }
        if (updatedData.containsKey("courseNumber")) {
            validateCourseNumberField("courseNumber", updatedData, errors);
        }
        if (updatedData.containsKey("dateOfBirth")) {
            validateDateOfBirth("dateOfBirth", updatedData, errors);
        }
        if (updatedData.containsKey("professor")) {
            validateBooleanField("professor", updatedData, errors);
        }
        if (updatedData.containsKey("consulting")) {
            validateBooleanField("consulting", updatedData, errors);
        }

        return errors;
    }

    private void validateCourseNumberField(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        validateNumberField(fieldName, updatedData, errors);
        Object value = updatedData.get(fieldName);
        int courseNumber;

        if (value instanceof Integer intValue) {
            courseNumber = intValue;
        } else {
            return;
        }

        if (courseNumber > 6) {
            errors.add(createFieldErrorDto(fieldName, null, "course.number.too.large"));
        }
    }

    private void validateDateOfBirth(String fieldName, Map<String, Object> updatedData, Set<FieldErrorDto> errors) {
        if (!updatedData.containsKey(fieldName)) {
            return;
        }

        Object value = updatedData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (!(value instanceof Number)) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
            return;
        }

        try {

            long timestamp = ((Number) value).longValue();
            LocalDate date = Instant.ofEpochMilli(timestamp * 1000)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate today = LocalDate.now();

            if (date.isAfter(today)) {
                errors.add(createFieldErrorDto(fieldName, null, "date.of.birth.after.today"));
            }

        } catch (Exception e) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
        }
    }

    public void validatePolitics(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        if (!Boolean.TRUE.equals(userData.get("acceptedPrivacyPolicy"))) {
            errors.add(createFieldErrorDto("acceptedPrivacyPolicy", null,
                    "not.accepted.privacy.policy"));
        }

        if (!Boolean.TRUE.equals(userData.get("acceptedPersonalDataProcessing"))) {
            errors.add(createFieldErrorDto("acceptedPersonalDataProcessing", null,
                    "not.accepted.personal.data.processing"));
        }
    }

    public void validateFirstName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("firstName", userData, errors);
    }

    public void validateLastName(Map<String, Object> userData, Set<FieldErrorDto> errors) {
        validateTextField("lastName", userData, errors);
    }

    private void validateEmptyField(String fieldName,
                                    String value, Set<FieldErrorDto> errors) {

        boolean isRequired = isRequired(fieldName);

        if ((value == null || value.isBlank()) && isRequired) {
            errors.add(createFieldErrorDto(fieldName, null, "field.empty"));
        }

    }

    private void validateTooLongField(String fieldName,
                                      String value, Set<FieldErrorDto> errors) {

        if (value != null && value.length() > FIELD_MAX_LENGTH) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.too.long"));
        }
    }

    private void validateForbiddenSymbols(String fieldName,
                                          String value, Set<FieldErrorDto> errors) {

        if (value != null && !value.matches(ACCEPTABLE_SYMBOLS_PATTERN)) {
            errors.add(createFieldErrorDto(fieldName, null,
                    "field.has.forbidden.symbols"));
        }
    }

    private void validateTextField(String fieldName,
                                   Map<String, Object> userData, Set<FieldErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (!(value instanceof String strValue)) {
            errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
            return;
        }

        validateEmptyField(fieldName, strValue, errors);

        if (strValue.isEmpty()) {
            return;
        }

        validateTooLongField(fieldName, strValue, errors);
        validateForbiddenSymbols(fieldName, strValue, errors);
    }

    public void validateDomainName(String email, Set<FieldErrorDto> errors) {

        if (email == null || !email.contains("@")) {
            return;
        }

        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        boolean isDomainExists = educationalInstitutionRepository.findByDomenName(domain).isPresent();

        if (!isDomainExists) {
            errors.add(createFieldErrorDto(
                    "email", new Object[]{domain}, "institution.domain.not.registered"
            ));
        }
    }

    private void validateBooleanField(String fieldName,
                                      Map<String, Object> userData, Set<FieldErrorDto> errors) {
        if (!userData.containsKey(fieldName)) {
            return;
        }

        Object value = userData.get(fieldName);

        if (value instanceof Boolean) {
            return;
        }
        if (value instanceof String) {
            String strValue = ((String) value).toLowerCase(Locale.ROOT);
            if (strValue.equals("true") || strValue.equals("false")) {
                return;
            }
        }

        errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
    }

    private void validateNumberField(String fieldName,
                                     Map<String, Object> userData, Set<FieldErrorDto> errors) {

        Object value = userData.get(fieldName);

        if (!isRequired(fieldName) && value == null) {
            return;
        }

        if (value instanceof Integer intValue) {
            if (intValue < 0) {
                errors.add(createFieldErrorDto(fieldName, null, "field.invalid.value"));
            }
            return;
        }
        errors.add(createFieldErrorDto(fieldName, null, "field.invalid.type"));
    }

    public void validateEmailField(String email, Set<FieldErrorDto> errors) {

        if (email == null || email.isBlank()) {
            errors.add(createFieldErrorDto(
                    "email", null,
                    "field.empty"));
            return;
        }

        EmailValidator validator = EmailValidator.getInstance(false, true);

        if (!validator.isValid(email)) {
            errors.add(createFieldErrorDto(
                    "email", null,
                    "email.format.incorrect"));
        }

        validateDomainName(email, errors);
    }

    private FieldErrorDto createFieldErrorDto(String field, Object[] obj, String message) {
        return new FieldErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale())
        );
    }

    private FieldErrorDto createFieldErrorDto(String field, Object[] obj, String message,
                                              UUID objectId) {
        return new FieldErrorDto(
                field,
                messageSource.getMessage(message, obj, LocaleContextHolder.getLocale()),
                objectId
        );
    }

    public boolean checkIfEmailFree(String email) {
        return accountRepository.findByMainEmailContactContact(email).isEmpty();
    }

    public Set<FieldErrorDto> validateContactInfoForAdd(AccountEntity account, ContactInfoAddRequestDto request) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();
        String rawContact = request.getContact();
        ContactMethodType methodType = request.getContactMethodType();

        if (rawContact == null || rawContact.isBlank()) {
            errors.add(createFieldErrorDto(request.getContact(), null, "field.empty"));
            return errors;
        }

        String normalizedContact = dataNormalisation.normalizeContact(request.getContact(), methodType);

        if (accountContactInfoRepository.existsByContactAndAccount(normalizedContact, account)) {
            errors.add(createFieldErrorDto(request.getContact(), null, "error.contact.already.exists"));
        }

        if (methodType == ContactMethodType.EMAIL) {
            validateEmailFieldForContactInfo(request.getContact(), errors, null);
        } else if (methodType == ContactMethodType.LINK) {
            validateLink(request.getContact(), errors, null);
        } else if (methodType == ContactMethodType.PHONE) {
            validatePhoneNumberField(request.getContact(), errors, null);
        } else {
            errors.add(createFieldErrorDto(rawContact, null, "method.unsupported.type"));
        }
        return errors;
    }

    public Set<FieldErrorDto> validateContactInfoForEdit(AccountEntity account, ContactInfoUpdateListRequestDto request) {
        Set<FieldErrorDto> errors = new LinkedHashSet<>();
        Set<String> seen = new HashSet<>();

        for (ContactInfoUpdateListRequestDto.ContactInfoUpdateRequestDto method : request.getAccountContactMethods()) {
            String contact = method.getContact();
            String trimmedContact = contact != null ? contact.trim().toLowerCase() : null;
            String methodType = method.getContactMethodType() != null ? method.getContactMethodType().toString() : "null";
            String key = methodType + "::" + (trimmedContact != null ? trimmedContact : "null");
            UUID contactId = method.getContactId();

            Optional<AccountContactInfoEntity> existingContactOpt = accountContactInfoRepository
                    .findById(method.getContactId());

            if (existingContactOpt.isPresent()) {
                AccountContactInfoEntity existingContact = existingContactOpt.get();

                boolean contactChanged = contact != null && !existingContact.getContact().equalsIgnoreCase(trimmedContact);
                boolean typeChanged = method.getContactMethodType() != null &&
                        !existingContact.getContactMethod().equals(method.getContactMethodType());
                boolean visibilityChanged = method.getVisibility() != null &&
                        !existingContact.getVisibility().equals(method.getVisibility());

                if (!contactChanged && !typeChanged && !visibilityChanged) {
                    continue;
                }
            }

            if (!seen.add(key)) {
                errors.add(createFieldErrorDto("contact", null, "error.contact.already.exists",
                        contactId));
                continue;
            }

            if (trimmedContact != null && accountContactInfoRepository.existsByContactAndAccount(trimmedContact, account)) {
                errors.add(createFieldErrorDto("contact", null, "error.contact.already.exists", contactId));
                continue;
            }

            if (method.getContactMethodType() != null && method.getContact() != null) {
                switch (method.getContactMethodType()) {
                    case EMAIL -> validateEmailFieldForContactInfo(method.getContact(), errors, contactId);
                    case LINK -> validateLink(method.getContact(), errors, contactId);
                    case PHONE -> validatePhoneNumberField(method.getContact(), errors, contactId);
                }
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

        validateTooLongField(contact, contact, errors);
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
            errors.add(createFieldErrorDto(field, args, code, objectId));
        } else {
            errors.add(createFieldErrorDto(field, args, code));
        }
    }
}
