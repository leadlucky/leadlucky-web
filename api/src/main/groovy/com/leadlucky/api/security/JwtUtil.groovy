package com.leadlucky.api.security

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.User
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication

class JwtUtil {

    static String getToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact()
    }


    static String parseForUsername(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET.getBytes())
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject()
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(
                    message: "Expired or invalid JWT token",
                    httpStatus: HttpStatus.UNAUTHORIZED
            )
        }
    }

}