package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD log repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface LogRepository extends CrudRepository<Log, Integer> {
    /**
     * Method for getting all logs divided into pages
     * @param pageable page details
     * @return {@link org.springframework.data.domain.Page}<{@link org.dev.cash_accounts_manager_backend.models.Log}>
     */
    Page<Log> findAll(Pageable pageable);
}
