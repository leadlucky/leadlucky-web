package com.leadlucky.api.util

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class AuthUtil {

    static Authentication getAuth() {
        return SecurityContextHolder.getContext().authentication
    }

    static boolean hasAdminAccess(Authentication auth) {
        return auth.authorities.findIndexOf({ it -> it.authority == "ROLE_ADMIN" }) > -1
    }

    static void validateAdminAccess() {
        Authentication auth = getAuth()
        if (!hasAdminAccess(auth)) {
            throw new AccessDeniedException("${auth.name} does not have admin role.")
        }
    }

    static void validateAccessToUser(String username) {
        Authentication auth = getAuth()
        if (username != auth.name && !hasAdminAccess(auth)) {
            throw new AccessDeniedException("${auth.name} does not have access to ${username}")
        }
    }

}