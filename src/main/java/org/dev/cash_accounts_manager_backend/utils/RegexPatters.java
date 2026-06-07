package org.dev.cash_accounts_manager_backend.utils;

public class RegexPatters {
    public static final String CHARACTERS_REGEX_PATTERN = "[a-zA-Z ]+";
    public static final String CAPITAL_CHARACTERS_REGEX_PATTERN = "[A-Z ]+";
    public static final String CHARACTERS_NUMBERS_REGEX_PATTERN = "[a-zA-Z0-9 ]+";
    public static final String NUMBERS_REGEX_PATTERN = "[0-9]+";
    public static final String EMAIL_REGEX_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String PHONE_REGEX_PATTERN = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
}
