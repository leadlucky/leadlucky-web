package murraco.controller;

import murraco.dto.DateCountAggregate;
import murraco.exception.CustomException;
import murraco.model.Page;
import murraco.model.User;
import murraco.repository.PageRepository;
import murraco.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

@RestController
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping(value = "/{pageName}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public Page getPage(@PathVariable String pageName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return pageRepository.findByOwnerUsernameAndName(auth.getName(), pageName)
                .orElseThrow(() -> new CustomException("" +
                        "No page found with name: " + pageName + " for user " + auth.getName(),
                        HttpStatus.NOT_FOUND));
    }


    @GetMapping(value = "/{pageName}/countData/month/{date}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public Map<String, List<DateCountAggregate>> getPageTimeData(
            @PathVariable String pageName,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") Date date) throws ParseException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!pageRepository.existsByOwnerUsernameAndName(auth.getName(), pageName)) {
            throw new CustomException(
                    "User " + auth.getName() + " does not own the page " + pageName,
                    HttpStatus.UNAUTHORIZED
            );
        }

        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTime(date);
        endDateCal.add(Calendar.MONTH, 1);
        Date endDate = Date.from(endDateCal.toInstant());

        List<DateCountAggregate> views = pageRepository.findDailyPageViewCounts(pageName, date, endDate);
        Map<String, List<DateCountAggregate>> response = new HashMap<>();
        response.put("views", views);
        // TODO aggregate emails
        return response;
    }


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public List<Page> getUserPages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.getName()));

        return user.getPages();
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
