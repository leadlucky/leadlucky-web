package com.leadlucky.api.service

import com.google.api.services.analyticsreporting.v4.model.DateRange

interface AnalyticsService {

    /***
     * A more robust way to get a report of emails and views
     * where all hours of the day, or days of the year will
     * be filled in with zeros if necessary, as well as
     * automatically build  date ranges based on the string
     * input in either where yearly and monthly formats
     * will show results per day and the daily format will
     * show results per hour.
     * @param pageName name of the page to query counts for
     * @param date (in "yyyy", "yyyy-MM", or "yyyy-MM-dd" format)
     * @return map of timestamp to an inner map of field name to count name
     */
    Map getPageReport(String pageName, String date)

    /***
     * @param pageName name of the page to query counts for
     * @param dateRange DateRange representing time period to query for
     * @param timeDimension either GA_DATE or GA_DATE_HOUR for daily or hourly granularity
     * @return map of timestamp to view counts
     */
    Map getPageViewCounts(String pageName, DateRange dateRange, String timeDimension)

    /***
     * @param pageName name of the page to query counts for
     * @param dateRange DateRange representing time period to query for
     * @param timeDimension either GA_DATE or GA_DATE_HOUR for daily or hourly granularity
     * @return map of timestamp to email counts
     */
    Map getPageEmailCounts(String pageName, DateRange dateRange, String timeDimension)

}