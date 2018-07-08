package com.leadlucky.api.repository

import com.leadlucky.api.model.User
import org.springframework.data.jpa.repository.JpaRepository

import javax.transaction.Transactional
import java.util.Optional

interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username)

    boolean existsByUsernameOrEmail(String username, String email)

    Optional<User> findByUsername(String username)

    void deleteByUsername(String username)

}
