package org.dev.cash_accounts_manager_backend.utils;


import org.dev.cash_accounts_manager_backend.dtos.*;
import org.dev.cash_accounts_manager_backend.models.Log;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.account.ActionRecord;
import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;

public class Extensions {
    public static RoleDto asDto(Role role) {
        if (role == null) {
            return null;
        }

        return new RoleDto(role.getId(), role.getCode(), role.getDescription(), role.getCreatedAt(), role.getUpdatedAt());
    }

    public static Role asRole(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }

        return new Role(roleDto.id(), roleDto.code(), roleDto.description(), roleDto.createdAt(), roleDto.updatedAt());
    }

    public static UserDto asDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserDto(user.getId(), user.getFullName(), user.getUsername(), user.getPassword(),
                user.getCreatedAt(), user.getUpdatedAt(), asDto(user.getRole()), user.isDisabled());
    }

    public static User asUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return new User(userDto.id(), userDto.fullName(), userDto.username(), userDto.password(),
                userDto.createdAt(), userDto.updatedAt(), asRole(userDto.role()));
    }

    public static LogDto asDto(Log log) {
        if (log == null) {
            return null;
        }

        return new LogDto(log.getId(), log.getName(), asDto(log.getUser()), log.getObjects(), log.getDescription(), log.getCreatedAt());
    }

    public static Log asLog(LogDto logDto) {
        if (logDto == null) {
            return null;
        }

        return new Log(logDto.id(), logDto.name(), asUser(logDto.user()), logDto.objects(), logDto.description(), logDto.createdAt());
    }

    public static AddressDto asDto(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressDto(address.getId(), address.getStreet(), address.getHouseNumber(), address.getApartmentNumber(),
                address.getCity(), address.getState(), address.getZipCode(), address.getCountry());
    }

    public static Address asAddress(AddressDto addressDto) {
        if (addressDto == null) {
            return null;
        }

        return new Address(addressDto.id(), addressDto.street(), addressDto.houseNumber(), addressDto.apartmentNumber(), addressDto.city(),
                addressDto.state(), addressDto.zipCode(), addressDto.country());
    }

    public static PersonalInfoDto asDto(PersonalInfo personalInfo) {
        if (personalInfo == null) {
            return null;
        }

        return new PersonalInfoDto(personalInfo.getId(), Extensions.asDto(personalInfo.getOwner()),
                personalInfo.getFirstName(), personalInfo.getLastName(), personalInfo.getEmail(),
                personalInfo.getPhoneNumber(), asDto(personalInfo.getAddress()), personalInfo.getPersonalCode());
    }

    public static PersonalInfo asPersonalInfo(PersonalInfoDto personalInfoDto) {
        if (personalInfoDto == null) {
            return null;
        }

        return new PersonalInfo(Extensions.asUser(personalInfoDto.owner()),
                personalInfoDto.firstName(), personalInfoDto.lastName(), personalInfoDto.email(),
                personalInfoDto.phoneNumber(), asAddress(personalInfoDto.address()), personalInfoDto.personalCode());
    }

    public static ActionRecordDto asDto(ActionRecord actionRecord) {
        if (actionRecord == null) {
            return null;
        }

        return new ActionRecordDto(
                actionRecord.getId(),
                actionRecord.getExternalBankCode(),
                actionRecord.getExternalBankNumber(), actionRecord.getAdditionalAddressInfo(),
                actionRecord.getLabel(), actionRecord.getDescription(),
                actionRecord.getFundsAmount(), actionRecord.getRegistrationDateTime());
    }

    public static ActionRecord asActionRecord(ActionRecordDto actionRecordDto) {
        if (actionRecordDto == null) {
            return null;
        }

        return new ActionRecord(
                actionRecordDto.id(),
                actionRecordDto.externalBankCode(),
                actionRecordDto.externalBankNumber(), actionRecordDto.additionalAddressInfo(), actionRecordDto.label(),
                actionRecordDto.description(), actionRecordDto.fundsAmount(), actionRecordDto.registrationDateTime());
    }

    public static BankAccountDto asDto(BankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }

        return new BankAccountDto(
                bankAccount.getId(),
                asDto(bankAccount.getOwner()),
                bankAccount.getAccountName(), bankAccount.getBankType(),
                bankAccount.getAccountNumber(),
                bankAccount.getCurrentBalance(),
                bankAccount.getActionRecords().stream().map(Extensions::asDto).toList(),
                bankAccount.getBusinessCode()
        );
    }

    public static BankAccount asBankAccount(BankAccountDto bankAccountDto) {
        if (bankAccountDto == null) {
            return null;
        }

        return new BankAccount(
                bankAccountDto.id(),
                asUser(bankAccountDto.owner()),
                bankAccountDto.accountName(),
                bankAccountDto.bankType(),
                bankAccountDto.accountNumber(),
                bankAccountDto.currentBalance(),
                bankAccountDto.businessCode(),
                bankAccountDto.actionRecords().stream().map(Extensions::asActionRecord).toList()
        );
    }
}
