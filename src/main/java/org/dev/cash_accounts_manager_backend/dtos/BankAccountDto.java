package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.dev.cash_accounts_manager_backend.enums.BankType;

import java.util.List;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.account} DTO model
 *
 * @author Fabian Frontczak
 */
public record BankAccountDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        @NotNull(message = "Owner cannot be null")
        UserDto owner,

        @NotBlank(message = "Account name cannot be blank")
        String accountName,

        BankType bankType,

        @NotNull(message = "Personal info cannot be null")
        PersonalInfoDto personalInfo,

        @NotBlank(message = "Account number cannot be blank")
        String accountNumber,

        double currentBalance,

        List<ActionRecordDto> actionRecords,

        String businessCode
) { }
