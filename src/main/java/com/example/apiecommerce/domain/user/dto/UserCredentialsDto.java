package com.example.apiecommerce.domain.user.dto;

import java.util.Set;

public class UserCredentialsDto {
    private String email;
    private String password;
    private Set<String> roles;

    public UserCredentialsDto(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
