package com.leadlucky.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.leadlucky.api.model.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager

    JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager
    }

    @Override
    Authentication attemptAuthentication(HttpServletRequest req,
                                         HttpServletResponse res) throws AuthenticationException {
        try {
            User credential = new ObjectMapper().readValue(req.getInputStream(), User)

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credential.username, credential.password, [])
            )
        } catch (IOException e) {
            if(!(e instanceof MismatchedInputException))
              throw new RuntimeException(e)
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String token = JwtUtil.getToken((auth.getPrincipal() as org.springframework.security.core.userdetails.User).getUsername())
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token)
        res.writer.print(token)
    }
}