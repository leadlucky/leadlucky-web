package com.leadlucky.api

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class LeadluckyApiApplication {

    @Value('${ga-app-name}')
    String GA_APPLICATION_NAME

    @Value('${ga-key-path}')
    String GA_KEY_PATH

    static void main(String[] args) {
        SpringApplication.run(LeadluckyApiApplication.class, args)
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        new BCryptPasswordEncoder()
    }

    @Bean
    AnalyticsReporting initializeAnalyticsReporting() {

        def httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        def credential = GoogleCredential
                .fromStream(new FileInputStream(GA_KEY_PATH))
                .createScoped(AnalyticsReportingScopes.all())

        // Construct the Analytics Reporting service object.
        return new AnalyticsReporting.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(GA_APPLICATION_NAME).build()
    }


}
