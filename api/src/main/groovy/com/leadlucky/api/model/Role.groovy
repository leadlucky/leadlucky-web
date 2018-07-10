package com.leadlucky.api.model

import org.springframework.security.core.GrantedAuthority

enum Role implements GrantedAuthority {

    ROLE_ADMIN, ROLE_CLIENT, ROLE_PREMIUM

    String getAuthority() {
        return name()
    }
}