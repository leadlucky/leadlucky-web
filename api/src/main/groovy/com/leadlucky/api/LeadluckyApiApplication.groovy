package com.leadlucky.api

import org.modelmapper.ModelMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class LeadluckyApiApplication {

    static void main(String[] args) {
        SpringApplication.run(LeadluckyApiApplication.class, args)
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        new BCryptPasswordEncoder()
    }


}
