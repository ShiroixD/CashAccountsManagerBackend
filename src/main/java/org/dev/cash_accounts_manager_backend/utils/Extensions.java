package org.dev.cash_accounts_manager_backend.utils;


import org.dev.cash_accounts_manager_backend.dtos.ActionRecordDto;
import org.dev.cash_accounts_manager_backend.dtos.AddressDto;
import org.dev.cash_accounts_manager_backend.dtos.BankAccountDto;
import org.dev.cash_accounts_manager_backend.dtos.PersonalInfoDto;
import org.dev.cash_accounts_manager_backend.models.account.ActionRecord;
import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;

public class Extensions {
    public static AddressDto asDto(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressDto(address.getStreet(), address.getHouseNumber(), address.getApartmentNumber(),
                address.getCity(), address.getState(), address.getZipCode(), address.getCountry());
    }

    public static Address asAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }

        return new Address(addressDto.street(), addressDto.houseNumber(), addressDto.apartmentNumber(), addressDto.city(),
                addressDto.state(), addressDto.zipCode(), addressDto.country());
    }

    public static PersonalInfoDto asDto(PersonalInfo personalInfo) {
        if (personalInfo == null) {
            return null;
        }

        return new PersonalInfoDto(personalInfo.getFirstName(), personalInfo.getLastName(), personalInfo.getEmail(),
                personalInfo.getPhoneNumber(), asDto(personalInfo.getAddress()), personalInfo.getPersonalCode());
    }

    public static PersonalInfo asPersonalInfo(PersonalInfoDto personalInfoDto) {
        if (personalInfoDto == null) {
            return null;
        }

        return new PersonalInfo(personalInfoDto.firstName(), personalInfoDto.lastName(), personalInfoDto.email(),
                personalInfoDto.phoneNumber(), asAddress(personalInfoDto.address()), personalInfoDto.personalCode());
    }

    public static ActionRecordDto asDto(ActionRecord actionRecord) {
        if (actionRecord == null) {
            return null;
        }

        return new ActionRecordDto(actionRecord.getExternalBankCode(),
                actionRecord.getExternalBankNumber(), actionRecord.getAdditionalAddressInfo(),
                actionRecord.getLabel(), actionRecord.getDescription(),
                actionRecord.getFundsAmount(), actionRecord.getRegistrationDateTime());
    }

    public static ActionRecord asActionRecord(ActionRecordDto actionRecordDto) {
        if (actionRecordDto == null) {
            return null;
        }

        return new ActionRecord(actionRecordDto.externalBankCode(),
                actionRecordDto.externalBankNumber(), actionRecordDto.additionalAddressInfo(), actionRecordDto.label(),
                actionRecordDto.description(), actionRecordDto.fundsAmount(), actionRecordDto.registrationDateTime());
    }

    public static BankAccountDto asDto(BankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }

        return new BankAccountDto(bankAccount.getAccountName(), bankAccount.getBankType(),
                asDto(bankAccount.getPersonalInfo()), bankAccount.getAccountNumber(),
                bankAccount.getCurrentBalance(),
                bankAccount.getActionRecords().stream().map(Extensions::asDto).toList(),
                bankAccount.getBusinessCode());
    }

    public static BankAccount asPersonalAccount(BankAccountDto bankAccountDto) {
        if (bankAccountDto == null) {
            return null;
        }

        return new BankAccount(bankAccountDto.accountName(), bankAccountDto.bankType(),
                asPersonalInfo(bankAccountDto.personalInfo()), bankAccountDto.accountNumber(),
                bankAccountDto.currentBalance(),
                bankAccountDto.businessCode(),
                bankAccountDto.actionRecords().stream().map(Extensions::asActionRecord).toList());
    }
}
