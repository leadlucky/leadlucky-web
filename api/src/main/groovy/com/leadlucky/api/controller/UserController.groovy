package com.leadlucky.api.controller

import com.leadlucky.api.dto.ChargeDataDTO
import com.leadlucky.api.dto.UserDataDTO
import com.leadlucky.api.dto.UserLogin
import com.leadlucky.api.dto.UserResponseDTO
import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Page
import com.leadlucky.api.model.Role
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.service.UserService
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Customer
import com.stripe.model.Subscription
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private UserService userService

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PageRepository pageRepository


    @Autowired
    private ModelMapper modelMapper

    @PostMapping("/signin")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    String login(@RequestBody UserLogin creds) {
        return userService.signIn(creds.username, creds.password)
    }

    @PostMapping("/signup")
    String signup(@RequestBody UserDataDTO user) {
        return userService.signUp(modelMapper.map(user, User.class))
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    String delete(@PathVariable String username) {
        userService.delete(username)
        return username
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    UserResponseDTO search(@PathVariable String username) {
        return modelMapper.map(userService.search(username), UserResponseDTO.class)
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    User getCurrentUser() {
        Authentication auth = SecurityContextHolder.context.authentication
        String username = auth.name

        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        if (user.stripeCustomerId != null) {
            try {
                Stripe.apiKey = "sk_test_XqjOE25ia1m5Kp4FRWZ78GR2"

                Customer stripeCustomer = Customer.retrieve(user.stripeCustomerId)
                Subscription stripeSubscription = Subscription.retrieve(stripeCustomer.subscriptions.data.get(0).id)

                user.premiumStatus = stripeSubscription.status

                userRepository.save(user)

            } catch (Exception e) {
                e.printStackTrace()
            }
        }

        return user
    }


    @PostMapping(value = "/upgrade")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    String upgrade(@RequestBody ChargeDataDTO chargeDataDTO) {

        Stripe.apiKey = "sk_test_XqjOE25ia1m5Kp4FRWZ78GR2"
        Authentication auth = SecurityContextHolder.context.authentication
        User user = userRepository.findByUsername(auth.name)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        if (user.stripeCustomerId != null) {
            //Add update customer here
            return "You're already premium?"
        }

        Map<String, Object> customerParams = new HashMap<>()
        customerParams.put("description", "Customer for LeadLucky")
        customerParams.put("source", chargeDataDTO.chargetoken) // obtained via Stripe.js

        Customer customer
        Subscription subscription

        try {
            customer = Customer.create(customerParams)
            subscription = Subscription.create([
                    "customer": customer.id,
                    "items"   : [
                            "0": [
                                    "plan": "plan_Cvo76gkcgiAIMh"
                            ]
                    ]
            ])
        } catch (StripeException e) {
            throw new CustomException(
                    message: "Error processing transaction with Stripe. Please try again later. ",
                    httpStatus: HttpStatus.INTERNAL_SERVER_ERROR
            )
        }

        user.stripeCustomerId = customer.id
        user.roles = [Role.ROLE_PREMIUM]
        user.premiumStatus = subscription.status
        userRepository.save(user)

        return "success"
    }

    @ResponseBody
    @RequestMapping(value = "/txt/{unique}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT') or hasRole('ROLE_PREMIUM')")
    String txtEmail(HttpServletResponse response, @PathVariable String unique) {
        Authentication auth = SecurityContextHolder.context.authentication

        Page tempTheme = pageRepository.findByName(unique).orElseThrow({
            new CustomException(
                    message: "Could not find page with name " + unique,
                    httpStatus: HttpStatus.NOT_FOUND
            )
        })

        User user = tempTheme.owner

        if (!user.username.equals(auth.name)) {
            return "Not your template." // TODO 401
        }

        String fileName = unique + ".txt"
        response.setHeader("Content-Disposition", "attachment filename=" + fileName)

        StringBuilder contentBuilder = new StringBuilder()

        tempTheme.emails.forEach({
            contentBuilder.append(it.email)
            contentBuilder.append("\r\n")
        })

        return contentBuilder.toString()
    }


}
