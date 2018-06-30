package murraco;

import murraco.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class JwtAuthServiceApp {

    @Autowired
    UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthServiceApp.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").allowedOrigins("*")
                        .allowedHeaders("*");
            }
        };
    }

//    @Override
//    public void run(String... params) throws Exception {
//        User admin = new User();
//        admin.setUsername("admin");
//        admin.setPassword("admin");
//        admin.setEmail("admin@email.com");
//        admin.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_ADMIN)));
//
//        userService.signUp(admin);
//
//        User client = new User();
//        client.setUsername("client");
//        client.setPassword("client");
//        client.setEmail("client@email.com");
//        client.setRoles(new ArrayList<Role>(Arrays.asList(Role.ROLE_CLIENT)));
//
//        userService.signUp(client);
//    }

}
