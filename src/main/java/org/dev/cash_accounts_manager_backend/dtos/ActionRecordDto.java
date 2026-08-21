package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.dev.cash_accounts_manager_backend.utils.RegexPatters;
import org.dev.cash_accounts_manager_backend.utils.ValidationMessageConsts;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.account.ActionRecord} DTO model
 *
 * @author Fabian Frontczak
 */
public record ActionRecordDto(
        @NotNull(message = "Id cannot be null")
        Integer id,

        @Min(value = 0, message = "External bank code must be positive")
        int externalBankCode,

        @NotBlank(message = "External bank number cannot be blank")
        @Pattern(
                regexp = RegexPatters.NUMBERS_REGEX_PATTERN,
                message = "External bank number " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_NUMBERS
        )
        String externalBankNumber,

        @Nullable
        String additionalAddressInfo,

        @NotBlank(message = "Label cannot be blank")
        @Pattern(
                regexp = RegexPatters.CHARACTERS_NUMBERS_REGEX_PATTERN,
                message = "Label  " + ValidationMessageConsts.NOT_BLANK + " and " + ValidationMessageConsts.MUST_CONTAIN_ONLY_CHARACTERS_AND_NUMBERS
        )
        String label,

        @Nullable
        String description,

        @NotNull(message = "Funds amount must be present")
        BigDecimal fundsAmount,

        @NotNull(message = "Registration date time cannot be null")
        LocalDateTime registrationDateTime
) { }
