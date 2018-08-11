package com.leadlucky.api.controller

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.service.impl.AnalyticsServiceImpl
import com.leadlucky.api.util.AuthUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("analytics/")
class AnalyticsController {


    @Autowired
    AnalyticsServiceImpl analyticsService

    @GetMapping("user")
    Map getUserStats() {
        def auth = AuthUtil.getAuth()
        if (!auth?.name) {
            throw new CustomException(
                    httpStatus: HttpStatus.UNAUTHORIZED,
                    message: "You must be logged in. "
            )
        }

        return analyticsService.getUserStats(auth.name)
    }

}
