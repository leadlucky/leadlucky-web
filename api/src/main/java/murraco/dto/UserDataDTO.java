package murraco.dto;

import lombok.Data;
import murraco.model.Role;

import java.util.Arrays;
import java.util.List;

@Data
public class UserDataDTO {

    private String username;

    private String password;

    private String email;

    private List<Role> roles = Arrays.asList(Role.ROLE_CLIENT);


}
