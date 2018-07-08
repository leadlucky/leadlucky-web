package com.leadlucky.api.service

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.UserRepository
import com.leadlucky.api.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    private JwtTokenProvider jwtTokenProvider

    @Autowired
    private AuthenticationManager authenticationManager

    String signIn(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password))
            return jwtTokenProvider.createToken(
                    username,
                    userRepository.findByUsername(username)
                            .orElseThrow(CustomException.getUserNotFoundHandler(username))
                            .roles)
        } catch (AuthenticationException e) {
            throw new CustomException(
                    message: "Invalid username/password supplied",
                    httpStatus: HttpStatus.UNAUTHORIZED
            )
        }
    }

    String signUp(User user) {
        if (!userRepository.existsByUsernameOrEmail(user.username, user.email)) {
            user.password = passwordEncoder.encode(user.password)
            user.premiumStatus = "unpaid"
            userRepository.save(user)
            return jwtTokenProvider.createToken(user.username, user.roles)
        } else {
            throw new CustomException("Username or email address is already in use", HttpStatus.BAD_REQUEST)
        }
    }

    void delete(String username) {
        userRepository.deleteByUsername(username)
    }

    User search(String username) throws CustomException {
        return userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(username))
    }

}
