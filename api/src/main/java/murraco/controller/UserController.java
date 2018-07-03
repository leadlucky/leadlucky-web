package murraco.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import murraco.dto.ChargeDataDTO;
import murraco.dto.UserDataDTO;
import murraco.dto.UserLogin;
import murraco.dto.UserResponseDTO;
import murraco.exception.CustomException;
import murraco.model.Page;
import murraco.model.Role;
import murraco.model.User;
import murraco.repository.PageRepository;
import murraco.repository.UserRepository;
import murraco.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;


    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/signin")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public String login(@RequestBody UserLogin creds) {
        return userService.signIn(creds.getUsername(), creds.getPassword());
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDataDTO user) {
        return userService.signUp(modelMapper.map(user, User.class));
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable String username) {
        userService.delete(username);
        return username;
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDTO search(@PathVariable String username) {
        return modelMapper.map(userService.search(username), UserResponseDTO.class);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.getName()));

        if (user.getStripeCustomerId() != null) {
            try {
                Stripe.apiKey = "sk_test_XqjOE25ia1m5Kp4FRWZ78GR2";

                Customer stripeCustomer = Customer.retrieve(user.getStripeCustomerId());
                Subscription stripeSubscription = Subscription.retrieve(stripeCustomer.getSubscriptions().getData().get(0).getId());

                user.setPremiumStatus(stripeSubscription.getStatus());

                userRepository.save(user);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }





    @PostMapping(value = "/upgrade")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public String upgrade(@RequestBody ChargeDataDTO chargeDataDTO) {

        Stripe.apiKey = "sk_test_XqjOE25ia1m5Kp4FRWZ78GR2";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.getName()));

        if (user.getStripeCustomerId() != null) {
            //Add update customer here
            return "You're already premium?";
        }

        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("description", "Customer for LeadLucky");
        customerParams.put("source", chargeDataDTO.getChargetoken()); // obtained via Stripe.js

        Customer customer = null;
        Subscription subscription = null;

        try {
            customer = Customer.create(customerParams);

            Map<String, Object> planStripeItem = new HashMap<>();
            planStripeItem.put("plan", "plan_Cvo76gkcgiAIMh");

            Map<String, Object> stripeItems = new HashMap<>();
            stripeItems.put("0", planStripeItem);

            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", customer.getId());
            subscriptionParams.put("items", stripeItems);

            subscription = Subscription.create(subscriptionParams);
        } catch (StripeException e) {
            throw new CustomException(
                    "Error processing transaction with Stripe. Please try again later. ",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        user.setStripeCustomerId(customer.getId());
        user.setRoles(new ArrayList<>(Arrays.asList(Role.ROLE_PREMIUM)));
        user.setPremiumStatus(subscription.getStatus());
        userRepository.save(user);

        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/txt/{unique}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    public String txtEmail(HttpServletResponse response, @PathVariable String unique) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Page tempTheme = pageRepository.findByName(unique).orElseThrow(
                () -> new CustomException("Could not find page with name " + unique, HttpStatus.NOT_FOUND)
        );

        User user = tempTheme.getOwner();

        if (!user.getUsername().equals(auth.getName())) {
            return "Not your template."; // TODO 401
        }

        String fileName = unique + ".txt";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        StringBuilder contentBuilder = new StringBuilder();

        tempTheme.getEmails().forEach(email -> {
            contentBuilder.append(email.getEmail());
            contentBuilder.append("\r\n");
        });

        return contentBuilder.toString();
    }


}
