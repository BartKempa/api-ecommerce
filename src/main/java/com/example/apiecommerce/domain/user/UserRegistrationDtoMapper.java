package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Component
public class UserRegistrationDtoMapper {
    private static final String DEFAULT_USER_ROLE = "USER";
    private final UserRoleRepository userRoleRepository;
    private final DateTimeProvider dateTimeProvider;

    public UserRegistrationDtoMapper(UserRoleRepository userRoleRepository, DateTimeProvider dateTimeProvider) {
        this.userRoleRepository = userRoleRepository;
        this.dateTimeProvider = dateTimeProvider;
    }


    UserRegistrationDto map(User user){
        return new UserRegistrationDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }

    User map(UserRegistrationDto userRegistrationDto){
        return new User(
                userRegistrationDto.getId(),
                userRegistrationDto.getEmail(),
                userRegistrationDto.getPassword(),
                userRegistrationDto.getFirstName(),
                userRegistrationDto.getLastName(),
                userRegistrationDto.getPhoneNumber(),
                dateTimeProvider.getCurrentTime(),
                userRoleRepository.findByName(DEFAULT_USER_ROLE).stream().collect(Collectors.toSet())
        );
    }
}
