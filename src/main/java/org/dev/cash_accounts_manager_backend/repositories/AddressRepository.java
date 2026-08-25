package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * CRUD address repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> { }
