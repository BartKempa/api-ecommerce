package com.example.apiecommerce.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public class UserUpdatePasswordDto {
        private Long id;
        @NotBlank
        private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
