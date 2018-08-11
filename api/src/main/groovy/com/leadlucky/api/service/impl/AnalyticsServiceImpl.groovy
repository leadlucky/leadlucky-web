package com.leadlucky.api.service.impl

import com.google.api.services.analyticsreporting.v4.model.DateRange
import com.leadlucky.api.repository.EmailRepository
import com.leadlucky.api.service.AnalyticsService
import com.leadlucky.api.util.MapUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.text.ParseException

@Service
class AnalyticsServiceImpl implements AnalyticsService {

    static final String GA_DATE = "ga:date"
    static final String GA_DATE_HOUR = "ga:dateHour"
    static final String GA_YEAR = "ga:year"

    // 1 = YEAR, 2 = MONTH, 3 = DAY_OF_MONTH (from java.util.Calendar)
    private static final Map<Integer, String> intervalFormats = [
            1: "yyyy",
            2: "yyyy-MM",
            5: "yyyy-MM-dd"
    ]

    private static final Map<Integer, String> intervalDimensions = [
            1: GA_DATE,
            2: GA_DATE,
            5: GA_DATE_HOUR
    ]


    @Autowired
    private EmailRepository emailRepository

    // TODO fill in zeroes for blank hours/days
    Map getPageReport(String pageName, String date) {
        def params = [
                pageName,
                getDateRange(date),
                intervalDimensions[getInterval(date)]
        ]

        // Merge email counts with view counts
        // and convert maps of timestamp -> count
        // to have inner maps with metric names
        return MapUtil.merge(
                getPageViewCounts(*params).collect({ ts, vc -> [ts, [views: vc]] }).collectEntries(),
                getPageEmailCounts(*params).collect({ ts, ec -> [ts, [emails: ec]] }).collectEntries()
        )
    }

    Map getPageViewCounts(String pageName, DateRange dateRange, String timeDimension) {
        return [:]
    }

    Map getPageEmailCounts(String pageName, DateRange dateRange, String timeDimension) {
        return [:]
    }

    private static int getInterval(String dateString) {
        def entry = intervalFormats.find({ int interval, String format ->
            if (dateString.length() != format.length())
                return false
            try {
                Date.parse(format, dateString)
            } catch (ParseException ignore) {
                return false
            }
            return true
        })

        if (!entry) {
            throw new IllegalArgumentException(
                    "Date is not in a valid format (${intervalFormats.collect({ k, v -> v }).join(", ")})."
            )
        }

        return entry['key'] as int
    }


    static DateRange getDateRange(String dateString) {
        def interval = getInterval(dateString)
        def format = intervalFormats[interval]

        def date = Date.parse(format, dateString)
        def c = Calendar.instance
        c.setTime(date)
        c.add(interval, 1)

        return new DateRange(
                startDate: date.format("yyyy-MM-dd"),
                endDate: new Date(c.getTimeInMillis()).format("yyyy-MM-dd")
        )
    }

}
