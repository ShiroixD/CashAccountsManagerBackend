package org.dev.cash_accounts_manager_backend.dtos;

import java.util.List;

/**
 * Generic DTO model for paged response body
 *
 * @author Fabian Frontczak
 */
public record PagedResponse<T>(
        List<T> content,
        long pageNumber,
        long pageSize,
        long pagesCount,
        long currentPageElementsCount,
        long totalElementsCount
) { }
