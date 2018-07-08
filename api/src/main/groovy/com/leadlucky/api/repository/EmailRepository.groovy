package com.leadlucky.api.repository

import com.leadlucky.api.dto.DateCountAggregate
import com.leadlucky.api.model.CollectedEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import java.util.Date
import java.util.List

interface EmailRepository extends JpaRepository<CollectedEmail, Long> {


    @Query("""
        SELECT
           new com.leadlucky.api.dto.DateCountAggregate(
               COUNT(*) as count,
               DATE(CONCAT(year(ce.createdDate), '-', month(ce.createdDate), '-', day(ce.createdDate))) as timestamp
           ) 
        FROM CollectedEmail AS ce
        WHERE
            ce.page.name = :pageName
            AND ce.createdDate between :startDate AND :endDate
        GROUP BY
            DATE(CONCAT(year(ce.createdDate), '-', month(ce.createdDate),'-', day(ce.createdDate)))
            
""")
    List<DateCountAggregate> findDailyEmailCounts(
            @Param("pageName") String pageName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate)

}
