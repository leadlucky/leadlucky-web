package com.leadlucky.api.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider

    JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider
    }

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            SecurityContextHolder
                    .context
                    .authentication = jwtTokenProvider.getAuthentication(token)
        }

        filterChain.doFilter(req, res)
    }

}
