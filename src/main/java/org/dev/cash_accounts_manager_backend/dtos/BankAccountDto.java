package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.enums.BankType;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

import java.math.BigDecimal;
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
        @Pattern(
                regexp = RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN,
                message = "Account name " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String accountName,

        BankType bankType,

        @NotBlank(message = "Account number cannot be blank")
        @Pattern(
                regexp = RegexPatters.NUMBERS_REGEX_PATTERN,
                message = "Account number  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_NUMBERS
        )
        String accountNumber,

        @NotNull(message = "Current balance cannot be null")
        BigDecimal currentBalance,

        List<ActionRecordDto> actionRecords,

        @Nullable
        @Pattern(
                regexp = RegexPatters.NUMBERS_REGEX_PATTERN,
                message = "Business code " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_NUMBERS
        )
        String businessCode
) { }
