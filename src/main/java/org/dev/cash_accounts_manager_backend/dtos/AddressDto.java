package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * {@link org.dev.cash_accounts_manager_backend.models.person} DTO model
 *
 * @author Fabian Frontczak
 */
public record AddressDto(
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
