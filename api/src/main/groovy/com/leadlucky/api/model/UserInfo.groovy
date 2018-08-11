package com.leadlucky.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty;

class UserInfo {

    boolean emailVerified

    String premiumStatus

    @JsonIgnore
    String stripeCustomerId

    @JsonIgnore
    String emailToken

}
