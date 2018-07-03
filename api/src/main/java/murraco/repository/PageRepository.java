package murraco.repository;

import murraco.dto.DateCountAggregate;
import murraco.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Integer> {

    String DAILY_AGGREGATION_QUERY = "" +
            " SELECT" +
            "   new murraco.dto.DateCountAggregate(" +
            "    COUNT(*) as count," +
            "    DATE(CONCAT(year(pv.createdDate), '-', month(pv.createdDate), '-', day(pv.createdDate))) as timestamp" +
            "   )" +
            " FROM PageView AS pv" +
            " WHERE" +
            "    pv.page.name = :pageName" +
            "    AND pv.createdDate between :startDate AND :endDate" +
            " GROUP BY" +
            "    DATE(CONCAT(year(pv.createdDate), '-', month(pv.createdDate),'-', day(pv.createdDate)))";

    List<Page> findByOwnerUsername(String username);

    Optional<Page> findByName(String name);

    Optional<Page> findByOwnerUsernameAndName(String username, String pageName);

    boolean existsByOwnerUsernameAndName(String username, String pageName);

    @Query(DAILY_AGGREGATION_QUERY)
    List<DateCountAggregate> findDailyPageViewCounts(
            @Param("pageName") String pageName,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

}
