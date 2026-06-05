package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.enums.RoleEnum;
import org.dev.cash_accounts_manager_backend.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CRUD role repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface RoleRepository  extends CrudRepository<Role, Integer> {
    /**
     * Method for role by given code
     * @param code account role
     * @return {@link java.util.Optional}<{@link org.dev.cash_accounts_manager_backend.models.Role}>
     */
    Optional<Role> findByCode(RoleEnum code);
}
