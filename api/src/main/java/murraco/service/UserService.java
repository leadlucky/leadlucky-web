package murraco.service;

import murraco.exception.CustomException;
import murraco.model.User;
import murraco.repository.UserRepository;
import murraco.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String signIn(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(
                    username,
                    userRepository.findByUsername(username)
                                  .orElseThrow(CustomException.getUserNotFoundHandler(username))
                                  .getRoles());
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNAUTHORIZED);
        }
    }

    public String signUp(User user) {
        if (!userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setPremiumStatus("unpaid");
            userRepository.save(user);
            return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        } else {
            throw new CustomException("Username or email address is already in use", HttpStatus.BAD_REQUEST);
        }
    }

    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    public User search(String username) throws CustomException {
        return userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(username));
    }

}
