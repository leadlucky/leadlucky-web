package murraco.dto;

import lombok.Data;
import murraco.model.Role;

import java.util.List;

@Data
public class UserResponseDTO {

    private Integer id;

    private String username;

    private String email;

    private String premiumStatus;

    private List<Role> roles;

}
