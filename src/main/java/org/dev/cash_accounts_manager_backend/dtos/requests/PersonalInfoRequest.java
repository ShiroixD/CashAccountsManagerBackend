package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Personal info model for creation request
 *
 * @author Fabian Frontczak
 */
public record PersonalInfoRequest(
        @NotNull(message = "Owner cannot be null")
        Integer ownerId,

        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        String email,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        AddressRequest address,

        @NotBlank(message = "Personal code cannot be blank")
        String personalCode
) { }
