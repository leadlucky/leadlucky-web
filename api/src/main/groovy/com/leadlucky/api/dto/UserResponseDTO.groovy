package com.leadlucky.api.dto


import com.leadlucky.api.model.Role

import java.util.List

class UserResponseDTO {

    Integer id

    String username

    String email

    String premiumStatus

    List<Role> roles

}
