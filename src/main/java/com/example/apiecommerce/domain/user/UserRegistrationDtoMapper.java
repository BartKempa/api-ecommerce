package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;

public class UserRegistrationDtoMapper {

    static UserRegistrationDto map(User user){
        return new UserRegistrationDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }
}
