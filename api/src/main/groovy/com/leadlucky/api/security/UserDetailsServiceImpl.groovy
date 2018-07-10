package com.leadlucky.api.security

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.User
import com.leadlucky.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository

    @Override
    UserDetails loadUserByUsername(String username) throws NoSuchElementException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(CustomException.getUserNotFoundHandler(username))

        return new org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                true, true, true, true,
                user.roles.collect({ role -> new SimpleGrantedAuthority(role.name()) })
        )
    }

}
