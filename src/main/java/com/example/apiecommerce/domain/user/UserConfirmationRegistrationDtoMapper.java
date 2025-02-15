package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.user.dto.UserConfirmationRegistrationDto;
import org.springframework.stereotype.Component;

@Component
public class UserConfirmationRegistrationDtoMapper {

    UserConfirmationRegistrationDto map(User user){
        return new UserConfirmationRegistrationDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }
}
