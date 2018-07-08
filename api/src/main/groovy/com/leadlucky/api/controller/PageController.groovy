package com.leadlucky.api.controller

import com.leadlucky.api.dto.DateCountAggregate
import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Page
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.EmailRepository
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

import java.text.ParseException
import java.util.*

@RestController
@RequestMapping("/pages")
class PageController {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PageRepository pageRepository

    @Autowired
    private EmailRepository emailRepository

    @GetMapping(value = "/{pageName}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    Page getPage(@PathVariable String pageName) {
        Authentication auth = SecurityContextHolder.context.authentication
        return pageRepository.findByOwnerUsernameAndName(auth.name, pageName)
                .orElseThrow({new CustomException(
                        message: "No page found with name: $pageName for user $auth.name",
                        httpStatus: HttpStatus.NOT_FOUND)
        })
    }


    @GetMapping(value = "/{pageName}/countData/month/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    Map<String, List<DateCountAggregate>> getPageTimeData(
            @PathVariable String pageName,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date date) throws ParseException {
        Authentication auth = SecurityContextHolder.context.authentication
        if (!pageRepository.existsByOwnerUsernameAndName(auth.name, pageName)) {
            throw new CustomException(
                    message: "User $auth.name does not own the page $pageName",
                    httpStatus:  HttpStatus.UNAUTHORIZED
            )
        }

        Calendar endDateCal = Calendar.instance
        endDateCal.time = date
        endDateCal.add(Calendar.MONTH, 1)
        Date endDate = Date.from(endDateCal.toInstant())

        return [
                "views": pageRepository.findDailyPageViewCounts(pageName, date, endDate),
                "emails": emailRepository.findDailyEmailCounts(pageName, date, endDate)
        ]
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    List<Page> getUserPages() {
        Authentication auth = SecurityContextHolder.context.authentication

        User user = userRepository.findByUsername(auth.name)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        return user.pages
    }

    // TODO refactor...
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    boolean saveTheme(@RequestBody Page page) {
        try {
            Authentication auth = SecurityContextHolder.context.authentication
            User user = userRepository.findByUsername(auth.name)
                    .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

            user.addPage(page)

            userRepository.save(user)

            return true
        } catch (Exception e) {
            return false
        }
    }


}
