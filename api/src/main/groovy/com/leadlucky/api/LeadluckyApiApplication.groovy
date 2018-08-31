package com.leadlucky.api

import com.leadlucky.api.repository.UserCascadeSaveMongoEventListener
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
@EnableMongoAuditing
class LeadluckyApiApplication {

    static void main(String[] args) {
        SpringApplication.run(LeadluckyApiApplication.class, args)
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        new BCryptPasswordEncoder()
    }

    @Bean
    UserCascadeSaveMongoEventListener userCascadingMongoEventListener() {
        return new UserCascadeSaveMongoEventListener();
    }

}
