package com.leadlucky.api.service

import com.leadlucky.api.service.impl.AnalyticsServiceImpl
import org.junit.Test

import static junit.framework.Assert.assertEquals

class AnalyticsService_UT {

    @Test(expected = IllegalArgumentException)
    void badFormatDateRange(){
        AnalyticsServiceImpl.getDateRange("2018-01-01 12:00:00")
    }

    @Test
    void yearDateRange(){
        def dr = AnalyticsServiceImpl.getDateRange("2018")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2019-01-01", dr.endDate)
    }

    @Test
    void monthDateRange(){
        def dr = AnalyticsServiceImpl.getDateRange("2018-01")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2018-02-01", dr.endDate)
    }

    @Test
    void dayDateRange(){
        def dr = AnalyticsServiceImpl.getDateRange("2018-01-01")
        assertEquals("2018-01-01", dr.startDate)
        assertEquals("2018-01-02", dr.endDate)
    }

}
