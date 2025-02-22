package com.example.apiecommerce.domain.user;

import com.example.apiecommerce.domain.DateTimeProvider;
import com.example.apiecommerce.domain.address.Address;
import com.example.apiecommerce.domain.address.AddressDtoMapper;
import com.example.apiecommerce.domain.address.AddressRepository;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.order.Order;
import com.example.apiecommerce.domain.order.OrderDtoMapper;
import com.example.apiecommerce.domain.order.OrderRepository;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
import com.example.apiecommerce.domain.user.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserRoleRepository userRoleRepositoryMock;

    @Mock
    private DateTimeProvider dateTimeProviderMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private UserRegistrationDtoMapper userRegistrationDtoMapperMock;

    @Mock
    private AddressDtoMapper addressDtoMapperMock;

    @Mock
    private OrderDtoMapper orderDtoMapperMock;

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private UserConfirmationRegistrationDtoMapper userConfirmationRegistrationDtoMapperMock;

    private UserService userService;

    private static final String DEFAULT_USER_ROLE = "USER";

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepositoryMock, userRoleRepositoryMock, dateTimeProviderMock, passwordEncoderMock, userRegistrationDtoMapperMock, addressDtoMapperMock, orderDtoMapperMock, addressRepositoryMock, orderRepositoryMock, userConfirmationRegistrationDtoMapperMock);
    }

    @Test
    void shouldFindCredentialsByEmail() {
        //given
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setName("USER");
        Set<UserRole> userRoleSet = new HashSet<>();

        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("pass");
        user.setRoles(userRoleSet);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        Optional<UserCredentialsDto> credentialsByEmail = userService.findCredentialsByEmail("test@mail.com");

        //then
        assertTrue(credentialsByEmail.isPresent());
        assertEquals("test@mail.com", credentialsByEmail.get().getEmail());
        assertEquals("pass", credentialsByEmail.get().getPassword());
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("doesNotExist@mail.com")).thenReturn(Optional.empty());

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> userService.findCredentialsByEmail("doesNotExist@mail.com").orElseThrow());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        //given
        String email = null;

        //when & then
        assertThrows(IllegalArgumentException.class, () -> userService.findCredentialsByEmail(email));
    }

    @Test
    void shouldRegisterWithDefaultRole() {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@mail.com");
        userRegistrationDto.setFirstName("Bart");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("500123456");
        userRegistrationDto.setPassword("pass");

        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setName("USER");

        UserConfirmationRegistrationDto userConfirmationRegistrationDto = new UserConfirmationRegistrationDto();
        userConfirmationRegistrationDto.setEmail("test@mail.com");
        userConfirmationRegistrationDto.setFirstName("Bart");
        userConfirmationRegistrationDto.setLastName("Kowalski");
        userConfirmationRegistrationDto.setPhoneNumber("500123456");

        Mockito.when(userRoleRepositoryMock.findByName(DEFAULT_USER_ROLE)).thenReturn(Optional.of(userRole));
        Mockito.when(userConfirmationRegistrationDtoMapperMock.map(Mockito.any(User.class))).thenReturn(userConfirmationRegistrationDto);

        //when
        UserConfirmationRegistrationDto userConfirmationRegistrationDtoResult = userService.registerWithDefaultRole(userRegistrationDto);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertEquals("test@mail.com", savedUser.getEmail());
        assertEquals("Bart", savedUser.getFirstName());
        assertEquals("Kowalski", savedUser.getLastName());
        assertEquals("500123456", savedUser.getPhoneNumber());
        assertNotEquals("pass", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));

        assertEquals("test@mail.com", userConfirmationRegistrationDtoResult.getEmail());
    }

    @Test
    void shouldFindUserById() {
        //given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Bart");
        user.setLastName("Kowalski");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("500123456");
        user.setPassword("pass");

        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@mail.com");
        userRegistrationDto.setFirstName("Bart");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("500123456");
        userRegistrationDto.setPassword("pass");

        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRegistrationDtoMapperMock.map(user)).thenReturn(userRegistrationDto);

        //when
        Optional<UserRegistrationDto> userByIdOpt = userService.findUserById(1L);

        //then
        assertNotNull(userByIdOpt);
        assertTrue(userByIdOpt.isPresent());
        assertEquals("Bart", userByIdOpt.get().getFirstName());
        assertEquals("test@mail.com", userByIdOpt.get().getEmail());
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findById(1L);
        Mockito.verify(userRegistrationDtoMapperMock, Mockito.times(1)).map(user);
    }

    @Test
    void shouldThrowExceptionWhenTryFindNotExistsUserById() {
        //given
        long nonExistingUserId = 111L;

        Mockito.when(userRepositoryMock.findById(nonExistingUserId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(nonExistingUserId));

        //then
        assertEquals("User not found", exc.getMessage());
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findById(nonExistingUserId);
        Mockito.verifyNoInteractions(userRegistrationDtoMapperMock);
    }

    @Test
    void shouldUpdatePartialDetailsAboutUser() {
        //given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Bart");
        user.setLastName("Kowalski");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("500123456");
        user.setPassword("pass");

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("Bartosz");
        userUpdateDto.setLastName("Grzyb");

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        userService.updateUser("test@mail.com", userUpdateDto);

        //then
        assertEquals("Bartosz", user.getFirstName());
        assertEquals("Grzyb", user.getLastName());
        assertEquals("500123456", user.getPhoneNumber());
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail("test@mail.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        String nonExistingEmail = "notfound@mail.com";
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName("Bartosz");

        Mockito.when(userRepositoryMock.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

        //when + then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(nonExistingEmail, userUpdateDto));

        assertEquals("User not found", exception.getMessage());

        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail(nonExistingEmail);
        Mockito.verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    void shouldNotUpdateUserWhenAllFieldsNull() {
        //given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Bart");
        user.setLastName("Kowalski");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("500123456");
        user.setPassword("pass");

        UserUpdateDto userUpdateDto = new UserUpdateDto();

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        userService.updateUser("test@mail.com", userUpdateDto);

        //then
        assertEquals("Bart", user.getFirstName());
        assertEquals("Kowalski", user.getLastName());
        assertEquals("500123456", user.getPhoneNumber());
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail("test@mail.com");
    }
    @Test
    void shouldUpdateUserPassword() {
        //given
        User user = new User();
        user.setId(1L);
        user.setFirstName("Bart");
        user.setLastName("Kowalski");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("500123456");
        user.setPassword("oldPass");

        UserUpdatePasswordDto userUpdatePasswordDto = new UserUpdatePasswordDto();
        userUpdatePasswordDto.setId(1L);
        userUpdatePasswordDto.setPassword("Password123#");

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoderMock.encode("Password123#")).thenReturn("encodedPassword123#");

        //when
        userService.updateUserPassword("test@mail.com", userUpdatePasswordDto);

        //then
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail("test@mail.com");
        Mockito.verify(passwordEncoderMock, Mockito.times(1)).encode("Password123#");

        assertEquals("encodedPassword123#", user.getPassword());
    }

    @Test
    void shouldDeleteUser() {
        //given
        User user = new User();
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);
        address.setActive(true);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);

        user.setAddresses(Set.of(address));
        user.setOrders(Set.of(order));

        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));

        //when
        userService.deleteUser(1L);

        //then
        ArgumentCaptor<Collection<Order>> orderCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(orderRepositoryMock).deleteAll(orderCaptor.capture());
        assertEquals(1, orderCaptor.getValue().size());
        assertTrue(orderCaptor.getValue().contains(order));

        ArgumentCaptor<Collection<Address>> addressCaptor = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(addressRepositoryMock).deleteAll(addressCaptor.capture());
        assertEquals(1, addressCaptor.getValue().size());
        assertTrue(addressCaptor.getValue().contains(address));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).delete(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    void shouldThrowExceptionWhenDeleteNotExistUser() {
        //given
        long nonExistingUserId = 111L;
        Mockito.when(userRepositoryMock.findById(nonExistingUserId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(nonExistingUserId));

        //then
        assertTrue(exc.getMessage().contains("User not found"));

        Mockito.verifyNoInteractions(orderRepositoryMock, addressRepositoryMock);
        Mockito.verify(userRepositoryMock, Mockito.never()).delete(Mockito.any());
    }

    @Test
    void shouldDeleteUserWithNoOrdersOrAddresses() {
        //given
        User user = new User();
        user.setId(1L);
        user.setOrders(Collections.emptySet());
        user.setAddresses(Collections.emptySet());

        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user));

        //when
        userService.deleteUser(1L);

        //then
        Mockito.verify(orderRepositoryMock).deleteAll(Collections.emptySet());
        Mockito.verify(addressRepositoryMock).deleteAll(Collections.emptySet());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepositoryMock).delete(userCaptor.capture());
        assertEquals(user, userCaptor.getValue());
    }

    @Test
    void shouldFindTwoActiveUserAddresses() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Address address1 = new Address();
        address1.setId(1L);
        address1.setUser(user);
        address1.setActive(true);

        Address address2 = new Address();
        address2.setId(2L);
        address2.setUser(user);
        address2.setActive(true);

        user.setAddresses(Set.of(address1, address2));

        AddressDto addressDto1 = new AddressDto();
        addressDto1.setUserId(1L);
        addressDto1.setId(1L);

        AddressDto addressDto2 = new AddressDto();
        addressDto2.setUserId(1L);
        addressDto2.setId(2L);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(addressDtoMapperMock.map(address1)).thenReturn(addressDto1);
        Mockito.when(addressDtoMapperMock.map(address2)).thenReturn(addressDto2);

        //when
        List<AddressDto> allActiveUserAddressesResult = userService.findAllActiveUserAddresses("test@mail.com");

        //then
        assertEquals(2, allActiveUserAddressesResult.size());
        assertThat(allActiveUserAddressesResult).containsExactlyInAnyOrder(addressDto1, addressDto2);
    }

    @Test
    void shouldFindOneActiveUserAddress() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Address address1 = new Address();
        address1.setId(1L);
        address1.setUser(user);
        address1.setActive(true);

        Address address2 = new Address();
        address2.setId(2L);
        address2.setUser(user);
        address2.setActive(false);

        user.setAddresses(Set.of(address1, address2));

        AddressDto addressDto1 = new AddressDto();
        addressDto1.setUserId(1L);
        addressDto1.setId(1L);

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(addressDtoMapperMock.map(address1)).thenReturn(addressDto1);

        //when
        List<AddressDto> allActiveUserAddressesResult = userService.findAllActiveUserAddresses("test@mail.com");

        //then
        assertEquals(1, allActiveUserAddressesResult.size());
        assertThat(allActiveUserAddressesResult).containsExactly(addressDto1);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoAddresses() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setAddresses(Collections.emptySet());

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        List<AddressDto> allActiveUserAddressesResult = userService.findAllActiveUserAddresses("test@mail.com");

        //then
        assertTrue(allActiveUserAddressesResult.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasOnlyInactiveAddresses() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Address address1 = new Address();
        address1.setId(1L);
        address1.setUser(user);
        address1.setActive(false);

        Address address2 = new Address();
        address2.setId(2L);
        address2.setUser(user);
        address2.setActive(false);

        user.setAddresses(Set.of(address1, address2));

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        //when
        List<AddressDto> allActiveUserAddressesResult = userService.findAllActiveUserAddresses("test@mail.com");

        //then
        assertTrue(allActiveUserAddressesResult.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenUserDoesNotExist() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("nonexistent@mail.com")).thenReturn(Optional.empty());

        //when
        List<AddressDto> allActiveUserAddressesResult = userService.findAllActiveUserAddresses("nonexistent@mail.com");

        //then
        assertTrue(allActiveUserAddressesResult.isEmpty());
    }

    @Test
    void shouldFindTwoUserOrders() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(user);

        user.setOrders(Set.of(order1, order2));

        OrderFullDto orderFullDto1 = new OrderFullDto();
        orderFullDto1.setId(1L);
        orderFullDto1.setUserEmail("test@mail.com");

        OrderFullDto orderFullDto2 = new OrderFullDto();
        orderFullDto2.setId(2L);
        orderFullDto2.setUserEmail("test@mail.com");

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(orderDtoMapperMock.map(order1)).thenReturn(orderFullDto1);
        Mockito.when(orderDtoMapperMock.map(order2)).thenReturn(orderFullDto2);

        //when
        List<OrderFullDto> allUserOrdersResult = userService.findAllUserOrders("test@mail.com");

        //then
        assertEquals(2, allUserOrdersResult.size());
        assertThat(allUserOrdersResult).containsExactlyInAnyOrder(orderFullDto1, orderFullDto2);
    }

    @Test
    void shouldReturnEmptyListWhenTryFindAllOrderAndUserDoesNotExist() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("nonexistent@mail.com")).thenReturn(Optional.empty());

        //when
        List<OrderFullDto> allUserOrdersResult = userService.findAllUserOrders("nonexistent@mail.com");

        //then
        assertTrue(allUserOrdersResult.isEmpty());
    }

    @Test
    void shouldFindOneUserOrder() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUser(user);

        user.setOrders(Set.of(order1));

        OrderFullDto orderFullDto1 = new OrderFullDto();
        orderFullDto1.setId(1L);
        orderFullDto1.setUserEmail("test@mail.com");

        Mockito.when(userRepositoryMock.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(orderDtoMapperMock.map(order1)).thenReturn(orderFullDto1);

        //when
        List<OrderFullDto> allUserOrdersResult = userService.findAllUserOrders("test@mail.com");

        //then
        assertEquals(1, allUserOrdersResult.size());
        assertThat(allUserOrdersResult).containsExactly(orderFullDto1);
    }

    @Test
    void shouldNotInvokeMapperWhenUserDoesNotExist() {
        //given
        Mockito.when(userRepositoryMock.findByEmail("nonexistent@mail.com")).thenReturn(Optional.empty());

        //when
        List<OrderFullDto> allUserOrdersResult = userService.findAllUserOrders("nonexistent@mail.com");

        //then
        assertTrue(allUserOrdersResult.isEmpty());
        Mockito.verifyNoInteractions(orderDtoMapperMock);
    }
}