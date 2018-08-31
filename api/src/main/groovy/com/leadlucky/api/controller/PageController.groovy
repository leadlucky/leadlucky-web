package com.leadlucky.api.controller

import com.google.api.services.analyticsreporting.v4.model.DateRange
import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Page
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.service.AnalyticsService
import com.leadlucky.api.service.impl.AnalyticsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

import javax.validation.Valid
import java.text.ParseException

@RestController
@RequestMapping("/pages")
class PageController {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PageRepository pageRepository

    @Autowired
    private AnalyticsService analyticsService

    @GetMapping(value = "/{pageName}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    Page getPage(@PathVariable String pageName) {
        Authentication auth = SecurityContextHolder.context.authentication
        return pageRepository.findByOwnerUsernameAndName(auth.name, pageName)
                .orElseThrow({
            new CustomException(
                    message: "No page found with name: $pageName for user $auth.name",
                    httpStatus: HttpStatus.NOT_FOUND)
        })
    }

    @GetMapping(value = "/{pageName}/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    ResponseEntity getPageTimeData(
            @PathVariable String pageName) {
        Authentication auth = SecurityContextHolder.context.authentication

        def errHandler = {
            new CustomException(
                    message: "User $auth.name does not own a page called $pageName",
                    httpStatus: HttpStatus.NOT_FOUND
            )
        }

        def page = pageRepository.findByName(pageName).orElseThrow(errHandler)
        if (page.owner.username != auth.name) {
            throw errHandler()
        }

        return ResponseEntity.ok([
                emails: page.emails.size(),
                views : analyticsService.getPageViewCounts(
                        pageName,
                        new DateRange(
                                startDate: "2010-01-01",
                                endDate: new Date().format("yyyy-MM-dd")
                        ),
                        AnalyticsServiceImpl.GA_YEAR
                ).collect({ k, v -> v }).sum()
        ])
    }

    @GetMapping(value = "/{pageName}/stats/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    ResponseEntity getPageTimeData(
            @PathVariable String pageName,
            @PathVariable String date) throws ParseException {
        Authentication auth = SecurityContextHolder.context.authentication
        if (!pageRepository.existsByOwnerUsernameAndName(auth.name, pageName)) {
            throw new CustomException(
                    message: "User $auth.name does not own the page $pageName",
                    httpStatus: HttpStatus.UNAUTHORIZED
            )
        }

        return ResponseEntity.ok(analyticsService.getPageReport(pageName, date))
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    List<Page> getUserPages() {
        Authentication auth = SecurityContextHolder.context.authentication

        User user = userRepository.findByUsername(auth.name)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        return user.pages
    }

    // TODO return something better... HATEOAS?
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    boolean saveTheme(@RequestBody @Valid Page page) {
        Authentication auth = SecurityContextHolder.context.authentication
        def user = userRepository.findByUsername(auth.name)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        user.pages.add(page)
        userRepository.save(user)

        return true
    }


}
