package com.leadlucky.api.controller

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Page
import com.leadlucky.api.model.Role
import com.leadlucky.api.model.User
import com.leadlucky.api.model.api.ChargeDataDTO
import com.leadlucky.api.repository.PageRepository
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.security.JwtUtil
import com.leadlucky.api.service.impl.UserServiceImpl
import com.leadlucky.api.util.AuthUtil
import com.leadlucky.api.util.ResponseUtil
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.Customer
import com.stripe.model.Subscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private UserServiceImpl userService

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PageRepository pageRepository


    @PostMapping("/signup")
    ResponseEntity registerUser(@RequestBody @Valid User user) {
        user.roles = [Role.ROLE_CLIENT] // Sanitize roles when doing normal registration
        userService.createUser(user)
        return ResponseEntity.ok(JwtUtil.getToken(user.username))
    }

    @GetMapping("/{username}")
    ResponseEntity getUser(@PathVariable String username) {
        AuthUtil.validateAccessToUser(username)
        return ResponseEntity.ok(userService.getUser(username))
    }

    @PutMapping("{username}")
    ResponseEntity updateUser(@PathVariable String username, @RequestBody @Valid User requestUser) {
        userService.updateUser(username, requestUser)
        return ResponseUtil.ok("Successfully updated user.")
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    String delete(@PathVariable String username) {
        userService.deleteUser(username)
        return username
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_PREMIUM')")
    User getCurrentUser() {
        def auth = SecurityContextHolder.context.authentication
        def username = auth.name

        def user = userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(auth.name))

        // Refresh premium status, check if subscription is still active
        // TODO - use web hooks to update this
        if (user.info.stripeCustomerId != null) {
            try {
                Stripe.apiKey = "sk_test_XqjOE25ia1m5Kp4FRWZ78GR2"
                def stripeCustomer = Customer.retrieve(user.stripeCustomerId)
                def stripeSubscription = Subscription.retrieve(stripeCustomer.subscriptions.data.get(0).id)
                if (user.info.premiumStatus != stripeSubscription.status) {
                    user.info.premiumStatus = stripeSubscription.status
                    userRepository.save(user)
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }

        return user
    }


    @PostMapping(value = "/upgrade")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_PREMIUM')")
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
        user.roles.add(Role.ROLE_PREMIUM)
        user.premiumStatus = subscription.status
        userRepository.save(user)

        return "success"
    }

    @ResponseBody
    @RequestMapping(value = "/txt/{unique}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_PREMIUM')")
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
