package org.dev.cash_accounts_manager_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO model for request containing string parameter
 *
 * @author Fabian Frontczak
 */
public record StringParameterRequest(
        @NotBlank(message = "String sentence is required")
        String sentence
) { }