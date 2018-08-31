package com.leadlucky.api.repository

import com.leadlucky.api.model.Page
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PageRepository extends CrudRepository<Page, Long> {

    Optional<Page> findByName(String name)

    Optional<Page> findByOwnerUsernameAndName(String username, String pageName)

    boolean existsByOwnerUsernameAndName(String username, String pageName)

}
