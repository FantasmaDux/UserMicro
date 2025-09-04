package io.github.pavelshe11.networkingmicro.constants;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.Set;

public class ValidationConstants {
    public static final int FIELD_MAX_LENGTH = 32;
    public static final int BIO_FIELD_MAX_LENGTH = 100;
    public static final String ACCEPTABLE_SYMBOLS_PATTERN = "^[a-zA-Zа-яА-ЯёЁ]+$";
    public static final String ACCEPTABLE_SYMBOLS_LINK_PATTERN = "^[a-zA-Z0-9.:_/?=&%#-]+$";

    public static final Set<String> ACCEPTABLE_TLDS = Set.of(".com", ".org", ".net", ".ru");
    public static final Set<String> REQUIRED_FIELDS = Set.of(
            "firstName", "email", "isProfessor", "isAdmin", "isVisible", "isConsulting", "lastName"
    );

}
