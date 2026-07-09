package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CRUD personal info repository interface for spring
 *
 * @author Fabian Frontczak
 */
@Repository
@Transactional
@EnableTransactionManagement
public interface PersonalInfoRepository extends CrudRepository<PersonalInfo, Integer> {
    /**
     * Method for getting user personal info
     * @param userId user id
     * @return {@link java.util.Optional}<{@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo}>
     */
    @Query(value = "select * from personal_Info where user_id = ?1", nativeQuery = true)
    Optional<PersonalInfo> findByOwner(Integer userId);

    /**
     * Method for counting personal info occurrences for user id
     * @param userId user id
     * @return {@link java.util.Optional}<{@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo}>
     */
    @Query(value = "select count(*) from personal_Info where user_id = ?1", nativeQuery = true)
    boolean countByUserId(Integer userId);

    /**
     * Method for checking if personal info with chosen personal code exists
     * @param personalCode personal code
     * @return boolean value
     */
    boolean existsByPersonalCode(String personalCode);
}
