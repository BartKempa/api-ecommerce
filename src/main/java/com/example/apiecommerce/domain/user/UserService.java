package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.address.AddressDtoMapper;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.user.dto.UserCredentialsDto;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
import com.example.apiecommerce.domain.user.dto.UserUpdatePasswordDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final String DEFAULT_USER_ROLE = "USER";
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final DateTimeProvider dateTimeProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationDtoMapper userRegistrationDtoMapper;
    private final AddressDtoMapper addressDtoMapper;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, DateTimeProvider dateTimeProvider, PasswordEncoder passwordEncoder, UserRegistrationDtoMapper userRegistrationDtoMapper, AddressDtoMapper addressDtoMapper) {
            this.userRepository = userRepository;
            this.userRoleRepository = userRoleRepository;
            this.dateTimeProvider = dateTimeProvider;
            this.passwordEncoder = passwordEncoder;
            this.userRegistrationDtoMapper = userRegistrationDtoMapper;
            this.addressDtoMapper = addressDtoMapper;

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
        user.setCreationDate(dateTimeProvider.getCurrentTime());
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

    @Transactional
    public void updateUserPassword(long id, UserUpdatePasswordDto userUpdatePasswordDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (userUpdatePasswordDto.getPassword() != null){
            user.setPassword(passwordEncoder.encode(userUpdatePasswordDto.getPassword()));
        }
        userRepository.save(user);
    }

    public List<AddressDto> findAllUserAddresses(long userId){
            return userRepository.findById(userId)
                    .map(User::getAddresses)
                    .orElse(Collections.emptySet())
                    .stream()
                    .map(addressDtoMapper::map)
                    .toList();
    }

}