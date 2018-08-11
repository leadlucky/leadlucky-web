package com.leadlucky.api.service

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.model.DateRange
import com.leadlucky.api.model.CollectedEmail
import com.leadlucky.api.model.Page
import com.leadlucky.api.repository.EmailRepository
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.service.impl.AnalyticsServiceImpl
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

import static com.leadlucky.api.service.impl.AnalyticsServiceImpl.getDateRange
import static junit.framework.TestCase.assertEquals

@RunWith(SpringRunner)
@SpringBootTest
class AnalyticsService_UT {

    @MockBean
    private AnalyticsReporting reportingService

    @Autowired
    private EmailRepository emailRepository

    @Autowired
    private PageRepository pageRepository

    @Autowired
    private AnalyticsService analyticsService

    static final String PAGE_NAME = "testPage"

    // There are 4 tests confirming getDateRange works
    // If it stops working they will fail, so I feel
    // comfortable using it to build test criteria
    static final DateRange YEAR_RANGE = getDateRange("2018")
    static final DateRange MONTH_RANGE = getDateRange("2018-01")
    static final DateRange DAY_RANGE = getDateRange("2018-01-01")

    @Before
    void setUp() {
        def page = pageRepository.save(new Page(
                name: PAGE_NAME, themeName: "test"
        ))

        emailRepository.saveAll([
                "2016-01-01 12:00": "olditem@testreport.net",
                "2018-01-01 12:00": "daylong@testreport.net",
                "2018-01-01 16:00": "daylong2@testreport.net",
                "2018-01-15 12:00": "monthlong@testreport.net",
                "2018-02-15 12:00": "yearlong@testreport.net"
        ].collect({ dateString, address ->
            new CollectedEmail(
                    page: page,
                    email: address,
                    createdDate: Date.parse("yyyy-MM-dd HH:00", dateString)
            )
        }))
    }

    @After
    void cleanUp() {
        emailRepository.deleteAll()
        pageRepository.deleteAll()

    }

    /* TODO test for a year long full report */
    /* TODO test for a month long full report */
    /* TODO test for a day long full report */

    /* TODO test for a year long views report */
    /* TODO test for a month long views report */
    /* TODO test for a day long views report */


    @Test
    void getEmailCountsAllTime() {
        assert analyticsService.getPageEmailCounts(
                PAGE_NAME,
                new DateRange(startDate: "2010-01-01", endDate: new Date().format("yyyy-MM-dd")),
                AnalyticsServiceImpl.GA_YEAR
        ) == [
                "2016": 1,
                "2018": 4
        ]
    }

    @Test
    void getEmailCountsForYear() {
        assert analyticsService.getPageEmailCounts(
                PAGE_NAME,
                YEAR_RANGE,
                AnalyticsServiceImpl.GA_DATE
        ) == [
                "2018-01-01": 2,
                "2018-01-15": 1,
                "2018-02-15": 1
        ]
    }

    @Test
    void getEmailCountsForMonth() {
        // For some reason assertEquals says identical maps are a failure...
        assert analyticsService.getPageEmailCounts(
                PAGE_NAME,
                MONTH_RANGE,
                AnalyticsServiceImpl.GA_DATE
        ) == [
                "2018-01-01": 2,
                "2018-01-15": 1
        ]
    }

    @Test
    void getEmailCountsForDay() {
        println analyticsService.getPageEmailCounts(
                PAGE_NAME,
                DAY_RANGE,
                AnalyticsServiceImpl.GA_DATE_HOUR
        )

        assert analyticsService.getPageEmailCounts(
                PAGE_NAME,
                DAY_RANGE,
                AnalyticsServiceImpl.GA_DATE_HOUR
        ) == [
                "2018-01-01 12:00": 1,
                "2018-01-01 16:00": 1
        ]
    }

    @Test(expected = IllegalArgumentException)
    void badFormatDateRange() {
        getDateRange("2018-01-01 12:00:00")
    }

    @Test
    void yearDateRange() {
        def dr = getDateRange("2018")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2019-01-01", dr.endDate)
    }

    @Test
    void monthDateRange() {
        def dr = getDateRange("2018-01")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2018-02-01", dr.endDate)
    }

    @Test
    void dayDateRange() {
        def dr = getDateRange("2018-01-01")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2018-01-02", dr.endDate)
    }




}
