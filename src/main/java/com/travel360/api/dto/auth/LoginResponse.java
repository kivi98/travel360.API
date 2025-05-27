package com.travel360.api.dto.auth;

import com.travel360.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}