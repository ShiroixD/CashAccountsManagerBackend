package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.account.ActionRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * CRUD action record repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
public interface ActionRecordRepository extends CrudRepository<ActionRecord, Integer> { }
