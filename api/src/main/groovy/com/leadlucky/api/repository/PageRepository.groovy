package com.leadlucky.api.repository

import com.leadlucky.api.dto.DateCountAggregate
import com.leadlucky.api.model.Page
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PageRepository extends JpaRepository<Page, Long> {


    List<Page> findByOwnerUsername(String username)

    Optional<Page> findByName(String name)

    Optional<Page> findByOwnerUsernameAndName(String username, String pageName)

    boolean existsByOwnerUsernameAndName(String username, String pageName)


    @Query("""
        SELECT
           new com.leadlucky.api.dto.DateCountAggregate(
               COUNT(*) as count,
               DATE(CONCAT(year(pv.createdDate), '-', month(pv.createdDate), '-', day(pv.createdDate))) as timestamp
           ) 
        FROM PageView AS pv
        WHERE
            pv.page.name = :pageName
            AND pv.createdDate between :startDate AND :endDate
        GROUP BY
            DATE(CONCAT(year(pv.createdDate), '-', month(pv.createdDate),'-', day(pv.createdDate)))
            
""")
    List<DateCountAggregate> findDailyPageViewCounts(
            @Param("pageName") String pageName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate)

}
