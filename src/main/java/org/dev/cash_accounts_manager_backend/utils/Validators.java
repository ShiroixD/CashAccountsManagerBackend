package org.dev.cash_accounts_manager_backend.utils;


import org.dev.cash_accounts_manager_backend.dtos.ActionRecordDto;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.requests.AddressRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.BankAccountCreationRequest;
import org.dev.cash_accounts_manager_backend.dtos.requests.PersonalInfoRequest;

import java.time.LocalDateTime;

public class Validators {
    private static final String NOT_BLANK = "cannot be blank";
    private static final String MUST_CONTAIN_ONLY_CHARACTERS = "must contain only characters\n";
    private static final String MUST_CONTAIN_ONLY_NUMBERS = "must contain only numbers\n";
    private static final String MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS = "must contain only characters and numbers\n";
    private static final int ISO_CODE_LENGTH = 2;

    public static String validate(AddressRequest addressRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        if (addressRequest == null) {
            return stringBuilder.toString();
        }

        String street = addressRequest.street();
        String houseNumber = addressRequest.houseNumber();
        String apartmentNumber = addressRequest.apartmentNumber();
        String city = addressRequest.city();
        String state = addressRequest.state();
        String zipCode = addressRequest.zipCode();
        String country = addressRequest.country();

        if (street == null || street.isBlank() || !street.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("Street " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (houseNumber == null || houseNumber.isBlank() || !houseNumber.matches(RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("House " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (apartmentNumber != null && !apartmentNumber.isBlank() && !apartmentNumber.matches(RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Apartment number " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (city != null && !city.isBlank() && !city.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("City " + MUST_CONTAIN_ONLY_CHARACTERS);
        }

        if (state != null && !state.isBlank() && !state.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("State " + MUST_CONTAIN_ONLY_CHARACTERS);
        }

        if (zipCode == null || zipCode.isBlank()) {
            stringBuilder.append("Zip code " + NOT_BLANK + "\n");
        }

        if (country == null || country.isBlank() || !country.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("Country " + MUST_CONTAIN_ONLY_CHARACTERS);
        }

        return stringBuilder.toString();
    }

    public static String validate(PersonalInfoRequest personalInfoRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        String firstName = personalInfoRequest.firstName();
        String lastName = personalInfoRequest.lastName();
        String email = personalInfoRequest.email();
        String phoneNumber = personalInfoRequest.phoneNumber();

        if (firstName == null || firstName.isBlank() || !firstName.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("First name  " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS);
        }

        if (lastName == null || lastName.isBlank() || !lastName.matches(RegexPatters.CHARACTERS_REGEX_PATTERN)) {
            stringBuilder.append("Last name  " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS);
        }

        if (email != null && !email.isBlank() && !email.matches(RegexPatters.EMAIL_REGEX_PATTERN)) {
            stringBuilder.append("Email must be of correct pattern");
        }

        if (phoneNumber == null || phoneNumber.isBlank() || !phoneNumber.matches(RegexPatters.PHONE_REGEX_PATTERN)) {
            stringBuilder.append("Phone number  " + NOT_BLANK + " must be of correct pattern");
        }

        return stringBuilder.toString();
    }

    public static String validate(ActionRecordDto actionRecordDto) {
        StringBuilder stringBuilder = new StringBuilder();

        int externalBankCode = actionRecordDto.externalBankCode();
        String externalBankNumber = actionRecordDto.externalBankNumber();
        String label = actionRecordDto.label();
        LocalDateTime registrationDateTime = actionRecordDto.registrationDateTime();

        if (externalBankCode <= 0) {
            stringBuilder.append("External bank code must be greater than zero\n");
        }

        if (externalBankNumber == null || externalBankNumber.isBlank() || !externalBankNumber.matches(RegexPatters.NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("External bank number " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_NUMBERS);
        }

        if (label == null || label.isBlank() || !label.matches(RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Label  " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (registrationDateTime == null) {
            stringBuilder.append("Registration date time must be present\n");
        }

        return stringBuilder.toString();
    }

    public static String validate(BankAccountDto bankAccountDto) {
        StringBuilder stringBuilder = new StringBuilder();

        String accountName = bankAccountDto.accountName();
        String accountNumber = bankAccountDto.accountNumber();
        double currentBalance = bankAccountDto.currentBalance();
        String businessCode = bankAccountDto.businessCode();

        if (accountName == null || accountName.isBlank() || !accountName.matches(RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Account name " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (accountNumber == null || accountNumber.isBlank() || !accountNumber.matches(RegexPatters.NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Account number  " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_NUMBERS);
        }

        if (currentBalance < 0) {
            stringBuilder.append("Current balance must be equal or greater than zero\n");
        }

        if (businessCode != null && !businessCode.isBlank() && !businessCode.matches(RegexPatters.NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Business code " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_NUMBERS);
        }

        return stringBuilder.toString();
    }

    public static String validate(BankAccountCreationRequest request) {
        StringBuilder stringBuilder = new StringBuilder();

        String accountName = request.accountName();
        String accountNumber = request.accountNumber();
        String businessCode = request.businessCode();

        if (accountName == null || accountName.isBlank() || !accountName.matches(RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Account name " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS);
        }

        if (accountNumber == null || accountNumber.isBlank() || !accountNumber.matches(RegexPatters.NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Account number  " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_NUMBERS);
        }

        if (businessCode != null && !businessCode.isBlank() && !businessCode.matches(RegexPatters.NUMBERS_REGEX_PATTERN)) {
            stringBuilder.append("Business code " + NOT_BLANK + " and " + MUST_CONTAIN_ONLY_NUMBERS);
        }

        return stringBuilder.toString();
    }
}
