package murraco.controller;

import murraco.exception.CustomException;
import murraco.model.CollectedEmail;
import murraco.model.Page;
import murraco.model.PageView;
import murraco.model.User;
import murraco.repository.PageRepository;
import murraco.repository.UserRepository;
import murraco.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping(value = "/{pageName}/data")
    public String getPageData(@PathVariable String pageName, HttpServletRequest req) {
        Page page = pageRepository.findByName(pageName)
                .orElseThrow(() -> new CustomException(
                        "No page found with name " + pageName,
                        HttpStatus.NOT_FOUND
                ));

        // Extract client IP Address from request
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = req.getRemoteAddr();

        // Create page view to record IP and timestamp
        PageView pageView = new PageView();
        pageView.setIpAddress(ipAddress);

        // Persist the page view
        page.addView(pageView);
        pageRepository.save(page);

        return page.getData();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public boolean saveTheme(@RequestBody Page page) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByUsername(auth.getName())
                    .orElseThrow(CustomException.getUserNotFoundHandler(auth.getName()));

            user.addPage(page);

            userRepository.save(user);

            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
