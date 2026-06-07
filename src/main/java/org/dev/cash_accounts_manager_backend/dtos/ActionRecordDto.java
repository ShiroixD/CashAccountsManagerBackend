package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.account.ActionRecord} DTO model
 *
 * @author Fabian Frontczak
 */
public record ActionRecordDto(
        int externalBankCode,

        @NotBlank(message = "External bank number cannot be blank")
        String externalBankNumber,

        String additionalAddressInfo,

        @NotBlank(message = "Label cannot be blank")
        String label,

        String description,

        double fundsAmount,

        @NotNull(message = "Registration date time cannot be null")
        LocalDateTime registrationDateTime
) { }
