package com.leadlucky.api.repository

import com.leadlucky.api.model.CollectedEmail
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

import javax.persistence.Tuple

interface EmailRepository extends CrudRepository<CollectedEmail, Long> {

    @Query("""
    SELECT
        COUNT(*) as emails,
        DATE_FORMAT(ce.createdDate, '%m-%d-%Y') as timestamp
    FROM CollectedEmail ce
    WHERE 
        ce.page.name = :pageName
        AND ce.createdDate BETWEEN :startDate AND :endDate
    GROUP BY DATE_FORMAT(ce.createdDate, '%m-%d-%Y')
    """)
    List<Tuple> getDailyEmailCounts(
            @Param("pageName") String pageName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate)

    @Query("""
    SELECT
        COUNT(*) as emails,
        DATE_FORMAT(ce.createdDate, '%m-%d-%Y %H:00') as timestamp
    FROM CollectedEmail ce
    WHERE
        ce.page.name = :pageName
        AND ce.createdDate BETWEEN :startDate AND :endDate
    GROUP BY DATE_FORMAT(ce.createdDate, '%m-%d-%Y %H:00')
    """)
    List<Tuple> getHourlyEmailCounts(
            @Param("pageName") String pageName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate)

}