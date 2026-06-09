package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.User;
import org.dev.cash_accounts_manager_backend.models.person.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD address repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface AddressRepository extends CrudRepository<Address, Integer> {
    /**
     * Method for getting all addresses divided into pages
     * @param pageable page details
     * @return {@link org.springframework.data.domain.Page}<{@link org.dev.cash_accounts_manager_backend.models.person.Address}>
     */
    @Query(value = "select * from addresses",
            countQuery = "select count(*) from addresses",
            nativeQuery = true)
    Page<User> findAllPaged(Pageable pageable);
}
