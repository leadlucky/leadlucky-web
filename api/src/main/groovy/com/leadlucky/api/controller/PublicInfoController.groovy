package com.leadlucky.api.controller

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.CollectedEmail
import com.leadlucky.api.model.Page
import com.leadlucky.api.repository.PageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/public")
class PublicInfoController {

    @Autowired
    private PageRepository pageRepository

    @GetMapping("pageData/{pageName}")
    ResponseEntity getPageData(@PathVariable String pageName, HttpServletRequest req) {
        Page page = pageRepository.findByName(pageName)
                .orElseThrow({
            new CustomException(
                    message: "No page found with name " + pageName,
                    httpStatus: HttpStatus.NOT_FOUND)
        })

        return ResponseEntity
                .ok()
                .header("Content-Type", "application/json")
                .body(page.data)
    }

    @PostMapping(value = "/{pageName}/email")
    Map saveEmail(
            @PathVariable String pageName,
            @RequestBody CollectedEmail email) {

        Page page = pageRepository.findByName(pageName)
                .orElseThrow({
            new CustomException(
                    message: "No page found with name " + pageName,
                    httpStatus: HttpStatus.NOT_FOUND)
        })

        page.addEmail(email)
        pageRepository.save(page)


        return [
                message: "Email address received."
        ]
    }

}
