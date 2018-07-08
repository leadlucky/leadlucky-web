package com.leadlucky.api.security

import com.leadlucky.api.model.User
import com.leadlucky.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository

    private static final User emptyUser

    static {
        emptyUser = new User()
        emptyUser.username = "none"
        emptyUser.password = "none"
        emptyUser.roles = new ArrayList<>()
    }

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(emptyUser)

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.password)
                .authorities(user.roles)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build()
    }

}
