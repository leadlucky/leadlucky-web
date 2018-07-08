package com.leadlucky.api.security

import com.leadlucky.api.exception.CustomException
import com.leadlucky.api.model.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider {

    // TODO MOVE THE SECRET KEY TO CONSUL
    @Value('${security.jwt.token.secret-key:secret-key}')
    private String secretKey

    @Value('${security.jwt.token.expire-length:360000000}')
    private long validityInMilliseconds = 360000000 // 1h

    @Autowired
    private DbUserDetailsService myUserDetails

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes())
    }

    String createToken(String username, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(username)
        claims.put("auth", roles.collect({ new SimpleGrantedAuthority(it.authority) }))

        Date now = new Date()
        Date validity = new Date(now.time + validityInMilliseconds)

        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact()
    }

    Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(parseUsername(token))
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private String parseUsername(String token) {
        return Jwts
                .parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
    }

    static String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization")
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length(), bearerToken.length())
        }
        return null
    }

    boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return true
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED)
        }
    }

}
