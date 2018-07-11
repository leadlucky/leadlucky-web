package com.leadlucky.api.service.impl

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.model.*
import com.leadlucky.api.repository.EmailRepository
import com.leadlucky.api.service.AnalyticsService
import com.leadlucky.api.util.MapUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.text.ParseException

@Service
class AnalyticsServiceImpl implements AnalyticsService {

    private static final String GA_DATE = "ga:date"
    private static final String GA_DATE_HOUR = "ga:date"

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
    private AnalyticsReporting reportingService

    @Autowired
    private EmailRepository emailRepositoy

    Map getPageViewReport(String pageName, String date) {

        // Break down hourly if it is a day's report, otherwise breakdown daily
        def timeDimension = intervalDimensions[getInterval(date)]
        def outputFormat = "yyyyMMdd${timeDimension == GA_DATE ? "" : "HH"}"
        def entryFormat = "MM-dd-yyyy${timeDimension == GA_DATE ? "" : " HH:00"}"
        def dateRange = getDateRange(date)

        // Create the GetReportsRequest object.
        def request = new GetReportsRequest(reportRequests: [
                new ReportRequest(
                        viewId: "178374466",
                        dateRanges: [dateRange],
                        metrics: [
                                metric("ga:pageviews", "views"),
                        ],
                        dimensions: [
                                dimension(timeDimension)
                        ],
                        dimensionFilterClauses: [
                                filter("ga:pagePath", "EXACT", "/$pageName")
                        ]
                )
        ])

        // Fetch the report from google analytics
        def gaResponse = reportingService.reports().batchGet(request).execute()

        def response = gaResponse.getReports()[0].getData().getRows().collect({ row ->
            [
                    Date.parse(outputFormat, row.getDimensions()[0]).format(entryFormat),
                    [views: row.getMetrics()[0].getValues()[0]]
            ]
        }).collectEntries()

        def sd = Date.parse("yyyy-MM-dd", dateRange.getStartDate())
        def ed = Date.parse("yyyy-MM-dd", dateRange.getEndDate())
        // TODO fill in zeroes for blank hours/days
        return MapUtil.merge(response, (timeDimension == GA_DATE ?
                emailRepositoy.getDailyEmailCounts(pageName, sd, ed)
                : emailRepositoy.getHourlyEmailCounts(pageName, sd, ed))
        .collect({
            [
                    it.get("timestamp"),
                    [emails: it.get("emails")]
            ]
        }).collectEntries())



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

    private static DimensionFilterClause filter(String dimensionName, String operatorName, String... expressions) {
        return new DimensionFilterClause(filters: [new DimensionFilter(
                dimensionName: dimensionName,
                operator: operatorName,
                expressions: expressions
        )])
    }

    private static Metric metric(String expression, String alias = null) {
        return new Metric(expression: expression, alias: alias)
    }

    private static Dimension dimension(String name) {
        new Dimension(name: name)
    }
}
