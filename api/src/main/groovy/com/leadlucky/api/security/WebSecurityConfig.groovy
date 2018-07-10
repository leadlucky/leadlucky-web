package com.leadlucky.api.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager()

        // Disable CSRF (cross site request forgery)
        http.cors().and().csrf().disable()

        // Allow only specified URLs
        http.authorizeRequests()
                .antMatchers(*SecurityConstants.UNSECURED_URLS).permitAll()
                .anyRequest().authenticated()

        // Register filters, give 401 when they fail
        http
                .addFilter(new JWTAuthenticationFilter(authenticationManager))
                .addFilter(new JWTAuthorizationFilter(authenticationManager, userDetailsService))
                .exceptionHandling().authenticationEntryPoint({
            req, res, ex -> res.sendError(HttpStatus.UNAUTHORIZED.value(), "Access denied. Please login. ")
        })
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
    }

    // TODO restrict CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()

        CorsConfiguration corsConfig = new CorsConfiguration()
        corsConfig.allowedHeaders = ["*"]
        corsConfig.allowedOrigins = ["*"]
        corsConfig.allowedMethods = ["GET", "PUT", "POST", "DELETE", "OPTIONS"]

        source.registerCorsConfiguration("/**", corsConfig)

        return source
    }


}