package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserRegistrationDtoMapper {
    private static final String DEFAULT_USER_ROLE = "USER";
    private final UserRoleRepository userRoleRepository;
    private final DataTimeProvider dataTimeProvider;

    public UserRegistrationDtoMapper(UserRoleRepository userRoleRepository, DataTimeProvider dataTimeProvider) {
        this.userRoleRepository = userRoleRepository;
        this.dataTimeProvider = dataTimeProvider;
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
                dataTimeProvider.getCurrentTime(),
                userRoleRepository.findByName(DEFAULT_USER_ROLE).stream().collect(Collectors.toSet())
        );
    }
}
