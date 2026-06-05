package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD user repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface UserRepository extends CrudRepository<User, Integer> {
    /**
     * Method for getting all active users
     * @return {@link java.util.List}<{@link org.dev.cash_accounts_manager_backend.models.User}>
     */
    @Query(value = "select * from users where disabled = false",
            nativeQuery = true)
    List<User> findAllActive();

    /**
     * Method for getting all active users divided into pages
     * @param pageable page details
     * @return {@link org.springframework.data.domain.Page}<{@link org.dev.cash_accounts_manager_backend.models.User}>
     */
    @Query(value = "select * from users where disabled = false",
            countQuery = "select count(*) from users where disabled = false",
            nativeQuery = true)
    Page<User> findAllActive(Pageable pageable);

    /**
     * Method for getting user by username
     * @return {@link java.util.Optional}<{@link org.dev.cash_accounts_manager_backend.models.User}>
     */
    Optional<User> findByUsername(String username);
}
