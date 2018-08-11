package com.leadlucky.api.service.impl

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.service.UserService
import com.leadlucky.api.util.PasswordUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder

    @Autowired
    private JavaMailSender mailSender

    @Value('${signup-message}')
    private String signupMessage

    @Value('${signup-subject}')
    private String signupSubject

    @Value('${email-address}')
    private String emailAddress

    @Override
    @Transactional
    void createUser(User user) {
        // Validate username isn't taken
        if (getUser(user.username)) {
            throw new CustomException(
                    message: "A user with username '${user.username}' already exists.",
                    httpStatus: HttpStatus.BAD_REQUEST
            )
        }

        validateAndUpdateUser(user)

            // Send verification email
            user.info.with {
                emailToken = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase()
                emailVerified = false
                premiumStatus = "unpaid"
            }

        if(emailAddress != "localhost@test.com") {
            mailSender.send(new SimpleMailMessage(
                    to: user.email,
                    from: emailAddress,
                    subject: signupSubject,
                    text: signupMessage.replace("{verifyurl}", "/verify?user=${user.username}&token=${user.info.emailToken}")
            ))
        }

        // Persist User
        userRepository.save(user)

        log.info("Created user ${user.username}")
    }

    @Override
    void updateUser(String username, User user) {
        findExistingUser(username)

        if (username != user.username) {
            throw new CustomException(
                    message: "Username cannot be changed.",
                    httpStatus: HttpStatus.BAD_REQUEST
            )
        }

        validateAndUpdateUser(user)

        userRepository.save(user)

        log.info("Updated user ${user.username}")

    }

    @Override
    void deleteUser(String username) {
        User user = findExistingUser(username)
        // TODO delete resources the user owns
        userRepository.delete(user)
        log.info("Deleted user ${user.username}")
    }

    @Override
    User getUser(String username) {
        return userRepository.findByUsername(username)
            .orElse(null)
    }

    @Override
    List<User> getUsers() {
        return userRepository.findAll()
    }

    @Override
    List<User> getUsers(int page, int length) {
        return userRepository.findAll(new PageRequest(page, length)).toList()
    }

    private void validateAndUpdateUser(User user) {
        if(!PasswordUtil.isUsernameValid(user.username)) {
            throw new CustomException(
                    message: "Username must between ${PasswordUtil.MIN_USERNAME}" +
                    " and ${PasswordUtil.MAX_USERNAME} characters and " +
                    " cannot contain spaces or special characters. ",
                    httpStatus: HttpStatus.BAD_REQUEST
            )
        }

        if (!PasswordUtil.isPasswordValid(user.password)) {
            throw new CustomException(
                    message: "Password must be between ${PasswordUtil.MIN_PASSWORD}" +
                    " and ${PasswordUtil.MAX_PASSWORD} characters," +
                    " contain both uppercase and lowercase letters," +
                    " a number, and a special character. ",
                    httpStatus: HttpStatus.BAD_REQUEST
            )
        }

        user.password = bCryptPasswordEncoder.encode(user.getPassword())
    }

    private User findExistingUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler())
    }


}
