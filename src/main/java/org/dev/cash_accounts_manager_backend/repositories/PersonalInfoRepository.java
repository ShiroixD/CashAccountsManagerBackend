package org.dev.cash_accounts_manager_backend.repositories;

import org.dev.cash_accounts_manager_backend.models.person.PersonalInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

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
     * Method for getting all personal info divided into pages
     * @param pageable page details
     * @return {@link org.springframework.data.domain.Page}<{@link org.dev.cash_accounts_manager_backend.models.person.PersonalInfo}>
     */
    @Query(value = "select * from personal_Info",
            countQuery = "select count(*) from personal_Info",
            nativeQuery = true)
    Page<PersonalInfo> findAllPaged(Pageable pageable);
}
