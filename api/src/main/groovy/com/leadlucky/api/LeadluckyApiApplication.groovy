package com.leadlucky.api

import com.leadlucky.api.service.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class LeadluckyApiApplication {

    @Autowired
    UserService userService

    static void main(String[] args) {
        SpringApplication.run(LeadluckyApiApplication.class, args)
    }

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper()
    }

//    @Override
//    void run(String... params) throws Exception {
//        User admin = new User()
//        admin.setUsername("admin")
//        admin.setPassword("admin")
//        admin.setEmail("admin@email.com")
//        admin.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_ADMIN)))
//
//        userService.signUp(admin)
//
//        User client = new User()
//        client.setUsername("client")
//        client.setPassword("client")
//        client.setEmail("client@email.com")
//        client.roles = new ArrayList<Role>(Arrays.asList(Role.ROLE_CLIENT))
//
//        userService.signUp(client)
//    }

}
