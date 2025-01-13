package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DataTimeProvider;
import com.example.apiecommerce.domain.user.dto.UserCredentialsDto;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

    @Service
    public class UserService {
        private static final String DEFAULT_USER_ROLE = "USER";
        private final UserRepository userRepository;
        private final UserRoleRepository userRoleRepository;
        private final DataTimeProvider dataTimeProvider;
        private final PasswordEncoder passwordEncoder;
        private final UserRegistrationDtoMapper userRegistrationDtoMapper;

        public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, DataTimeProvider dataTimeProvider, PasswordEncoder passwordEncoder, UserRegistrationDtoMapper userRegistrationDtoMapper) {
            this.userRepository = userRepository;
            this.userRoleRepository = userRoleRepository;
            this.dataTimeProvider = dataTimeProvider;
            this.passwordEncoder = passwordEncoder;
            this.userRegistrationDtoMapper = userRegistrationDtoMapper;
        }

        public Optional<UserCredentialsDto> findCredentialsByEmail(String email){
        return userRepository.findByEmail(email)
                .map(UserCredentialDtoMapper::map);
    }

    @Transactional
    public UserRegistrationDto registerWithDefaultRole(UserRegistrationDto userRegistrationDto){
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setPhoneNumber(userRegistrationDto.getPhoneNumber());
        user.setCreationDate(dataTimeProvider.getCurrentTime());
        UserRole userRole = userRoleRepository.findByName(DEFAULT_USER_ROLE).orElseThrow();
        user.getRoles().add(userRole);
        userRepository.save(user);
        return userRegistrationDtoMapper.map(user);
    }

    public Optional<UserRegistrationDto> findUserById(long userId){
       if (!userRepository.existsById(userId)){
           return Optional.empty();
       }
        return userRepository.findById(userId).map(userRegistrationDtoMapper::map);
    }

    @Transactional
    public void updateUser(long id, UserUpdateDto userUpdateDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (userUpdateDto.getFirstName() != null){
                user.setFirstName(userUpdateDto.getFirstName());
            }
        if (userUpdateDto.getLastName() != null){
            user.setLastName(userUpdateDto.getLastName());
        }
        if (userUpdateDto.getPhoneNumber() != null){
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(long id){
         if (!userRepository.existsById(id)){
             throw new EntityNotFoundException("User not found");
         }
         userRepository.deleteById(id);
    }
}
