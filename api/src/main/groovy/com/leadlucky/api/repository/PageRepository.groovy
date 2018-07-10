package com.leadlucky.api.repository

import com.leadlucky.api.model.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PageRepository extends JpaRepository<Page, Long> {


    Optional<Page> findByName(String name)

    Optional<Page> findByOwnerUsernameAndName(String username, String pageName)

    boolean existsByOwnerUsernameAndName(String username, String pageName)

}
