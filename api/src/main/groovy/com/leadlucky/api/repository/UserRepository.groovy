package com.leadlucky.api.repository

import com.leadlucky.api.model.User
import org.springframework.data.jpa.repository.JpaRepository


interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username)

}
