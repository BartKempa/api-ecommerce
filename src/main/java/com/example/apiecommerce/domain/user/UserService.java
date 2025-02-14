package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressDtoMapper;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.order.OrderDtoMapper;
import com.example.apiecommerce.domain.order.OrderRepository;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.user.dto.*;
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
    private final OrderDtoMapper orderDtoMapper;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final UserConfirmationRegistrationDtoMapper userConfirmationRegistrationDtoMapper;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, DateTimeProvider dateTimeProvider, PasswordEncoder passwordEncoder, UserRegistrationDtoMapper userRegistrationDtoMapper, AddressDtoMapper addressDtoMapper, OrderDtoMapper orderDtoMapper, AddressRepository addressRepository, OrderRepository orderRepository, UserConfirmationRegistrationDtoMapper userConfirmationRegistrationDtoMapper) {
            this.userRepository = userRepository;
            this.userRoleRepository = userRoleRepository;
            this.dateTimeProvider = dateTimeProvider;
            this.passwordEncoder = passwordEncoder;
            this.userRegistrationDtoMapper = userRegistrationDtoMapper;
            this.addressDtoMapper = addressDtoMapper;
            this.orderDtoMapper = orderDtoMapper;
            this.addressRepository = addressRepository;
            this.orderRepository = orderRepository;
            this.userConfirmationRegistrationDtoMapper = userConfirmationRegistrationDtoMapper;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email){
        return userRepository.findByEmail(email)
                .map(UserCredentialDtoMapper::map);
    }

    @Transactional
    public UserConfirmationRegistrationDto registerWithDefaultRole(UserRegistrationDto userRegistrationDto){
        User user = new User();
        user.setEmail(userRegistrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setPhoneNumber(userRegistrationDto.getPhoneNumber());
        user.setCreationDate(dateTimeProvider.getCurrentTime());
        UserRole userRole = userRoleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        user.getRoles().add(userRole);
        userRepository.save(user);
        return userConfirmationRegistrationDtoMapper.map(user);
    }

    public Optional<UserRegistrationDto> findUserById(long userId){
       return userRepository.findById(userId).map(userRegistrationDtoMapper::map);
    }

    @Transactional
    public void updateUser(String userMail, UserUpdateDto userUpdateDto){
        User user = userRepository.findByEmail(userMail)
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        orderRepository.deleteAll(user.getOrders());
        addressRepository.deleteAll(user.getAddresses());
        userRepository.delete(user);
    }

    @Transactional
    public void updateUserPassword(String userMail, UserUpdatePasswordDto userUpdatePasswordDto){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (userUpdatePasswordDto.getPassword() != null){
            user.setPassword(passwordEncoder.encode(userUpdatePasswordDto.getPassword()));
        }
        userRepository.save(user);
    }

    public List<AddressDto> findAllActiveUserAddresses(String userMail){
            return userRepository.findByEmail(userMail)
                    .map(User::getAddresses)
                    .orElse(Collections.emptySet())
                    .stream()
                    .filter(Address::isActive)
                    .map(addressDtoMapper::map)
                    .toList();
    }

    public List<OrderFullDto> findAllUserOrders(String userMail){
        return userRepository.findByEmail(userMail)
                .map(User::getOrders)
                .orElse(Collections.emptySet())
                .stream()
                .map(orderDtoMapper::map)
                .toList();
    }
}