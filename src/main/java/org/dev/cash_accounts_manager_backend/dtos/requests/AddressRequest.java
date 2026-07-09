package org.dev.cash_accounts_manager_backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * Address model for creation request
 *
 * @author Fabian Frontczak
 */
public record AddressRequest(
        @NotBlank(message = "Street cannot be blank")
        String street,

        @NotBlank(message = "House number cannot be blank")
        String houseNumber,

        String apartmentNumber,

        @NotBlank(message = "City cannot be blank")
        String city,

        @NotBlank(message = "State cannot be blank")
        String state,

        @NotBlank(message = "Zip code cannot be blank")
        String zipCode,

        @NotBlank(message = "Country cannot be blank")
        String country
) { }
