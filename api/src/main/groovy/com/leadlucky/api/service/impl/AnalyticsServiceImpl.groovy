package com.leadlucky.api.service.impl

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.model.*
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

    @Value('${ga-view-id}')
    private String GA_VIEW_ID

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
                getViewCounts(*params).collect({ts, vc -> [ts, [views: vc]]}).collectEntries(),
                getEmailCounts(*params).collect({ts, ec -> [ts, [emails: ec]]}).collectEntries()
        )
    }

    Map getViewCounts(String pageName, DateRange dateRange, String timeDimension) {
        // Decide whether the query results will include an hour we will need to parse
        def outputFormat = "yyyyMMdd${timeDimension == GA_DATE_HOUR ? "HH" : ""}"
        // Decide whether to display the hour dimension
        def entryFormat = "yyyy-MM-dd${timeDimension == GA_DATE_HOUR ? "HH" : ""}"

        // Create the GetReportsRequest object.
        def request = buildGetReportsRequest(pageName, GA_VIEW_ID, dateRange, timeDimension)

        // Fetch the report from google analytics
        def gaResponse = reportingService.reports().batchGet(request).execute()

        gaResponse.getReports()[0].getData().getRows().collect({ row ->
            [
                    Date.parse(outputFormat, row.getDimensions()[0]).format(entryFormat),
                    row.getMetrics()[0].getValues()[0]
            ]
        }).collectEntries()
    }

    Map getEmailCounts(String pageName, DateRange dateRange, String timeDimension) {
        emailRepositoy.getEmailCounts(
                pageName,
                Date.parse("yyyy-MM-dd", dateRange.getStartDate()),
                Date.parse("yyyy-MM-dd", dateRange.getEndDate()),
                timeDimension
        ).collect({
            [
                    it.get("timestamp"),
                    it.get("emails")
            ]
        }).collectEntries()
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

    static GetReportsRequest buildGetReportsRequest(
            String pageName,
            String viewId,
            DateRange dateRange,
            String timeDimension) {
        new GetReportsRequest(reportRequests: [
                new ReportRequest(
                        viewId: viewId,
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
