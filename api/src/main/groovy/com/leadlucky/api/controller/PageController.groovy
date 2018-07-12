package com.leadlucky.api.controller

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Page
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.service.AnalyticsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

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


    @GetMapping(value = "/{pageName}/countData/month/{date}")
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
