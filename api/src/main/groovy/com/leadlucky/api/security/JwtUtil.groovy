package com.leadlucky.api.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

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
                    .parseClaimsJws(token?.replace(SecurityConstants.TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject()
        } catch (JwtException | IllegalArgumentException ignore) {
            return null
        }
    }

}