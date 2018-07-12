package com.leadlucky.api.repository

import com.leadlucky.api.model.Page
import org.springframework.data.repository.CrudRepository

interface PageRepository extends CrudRepository<Page, Long> {


    Optional<Page> findByName(String name)

    Optional<Page> findByOwnerUsernameAndName(String username, String pageName)

    boolean existsByOwnerUsernameAndName(String username, String pageName)

}
