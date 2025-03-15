package com.example.apiecommerce.domain.user.dto;

import com.example.apiecommerce.domain.user.validation.PasswordCriteria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserUpdatePasswordDto {
        private Long id;
        @NotBlank
        @PasswordCriteria
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
