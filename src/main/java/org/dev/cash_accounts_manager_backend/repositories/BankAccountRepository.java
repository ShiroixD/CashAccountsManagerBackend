package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.account.BankAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD bank account repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface BankAccountRepository extends CrudRepository<BankAccount, Integer> {
    /**
     * Method for getting user bank accounts
     * @param userId user id
     * @return {@link java.util.List}<{@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo}>
     */
    @Query(value = "select * from bank_accounts where user_id = ?1", nativeQuery = true)
    List<BankAccount> findByOwner(Integer userId);

    /**
     * Method for getting user bank account with by name
     * @param userId user id
     * @return {@link java.util.List}<{@link org.dev.cash_accounts_manager_backend.models.account}>
     */
    @Query(value = "select * from bank_accounts where user_id = ?1 and accountName = ?2", nativeQuery = true)
    Optional<BankAccount> findByOwnerAndAccountName(Integer userId, String accountName);

    /**
     * Method for checking if account number already exists
     * @param accountNumber account number
     * @return boolean value telling if account number is taken
     */
    boolean existsByAccountNumber(String accountNumber);
}
