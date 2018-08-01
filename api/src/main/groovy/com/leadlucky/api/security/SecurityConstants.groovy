package com.leadlucky.api.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SecurityConstants {

    public static String SECRET

    public static long EXPIRATION_TIME

    public static String LOGIN_URL = "/login"
    public static String SIGNUP_URL = "/users/signup"

    public static List<String> UNSECURED_URLS = [
            LOGIN_URL,
            SIGNUP_URL,
            "/public/**",
//            "/",
//            "/index.html",
//            "/dist/*",
//            "/public/*",
//            "favicon-32x32.png"
    ]

    public static final String TOKEN_PREFIX = "Bearer "
    public static final String HEADER_STRING = "Authorization"

    @Value('${token-expiration}')
    void setExpirationTime(long expirationTime){
        EXPIRATION_TIME = expirationTime
    }

    // TODO load this from consul instead of a static file
    @Value('${token-gen-secret}')
    void setSecret(String secret){
        SECRET = secret
    }
}
