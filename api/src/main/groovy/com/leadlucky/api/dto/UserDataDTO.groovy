package com.leadlucky.api.dto


import com.leadlucky.api.model.Role

import java.util.Arrays
import java.util.List

class UserDataDTO {

    String username

    String password

    String email

    List<Role> roles = Arrays.asList(Role.ROLE_CLIENT)


}
