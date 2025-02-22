package com.example.apiecommerce.domain.address;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.address.dto.AddressUpdateDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private AddressDtoMapper addressDtoMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    private AddressService addressService;

    @BeforeEach
    void setUp() {
        addressService = new AddressService(addressRepositoryMock, addressDtoMapperMock, userRepositoryMock);
    }

    @Test
    void shouldSaveNewAddress() {
        // given
        User user = new User();
        user.setId(1L);

        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("Marszałkowska");
        addressDto.setBuildingNumber("10");
        addressDto.setApartmentNumber("5A");
        addressDto.setZipCode("00-001");
        addressDto.setCity("Warszawa");
        addressDto.setUserId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);

        AddressDto savedAddressDto = new AddressDto();
        savedAddressDto.setId(1L);
        savedAddressDto.setStreetName("Marszałkowska");
        savedAddressDto.setBuildingNumber("10");
        savedAddressDto.setApartmentNumber("5A");
        savedAddressDto.setZipCode("00-001");
        savedAddressDto.setCity("Warszawa");
        savedAddressDto.setUserId(1L);

        Mockito.when(addressDtoMapperMock.map(addressDto)).thenReturn(address);
        Mockito.when(addressRepositoryMock.save(Mockito.any(Address.class))).thenReturn(address);
        Mockito.when(addressDtoMapperMock.map(address)).thenReturn(savedAddressDto);

        // when
        AddressDto addressDtoResult = addressService.saveAddress(addressDto);

        // then
        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        Mockito.verify(addressRepositoryMock).save(addressArgumentCaptor.capture());
        Mockito.verify(addressRepositoryMock, Mockito.times(1)).save(Mockito.any(Address.class));

        Address result = addressArgumentCaptor.getValue();

        assertEquals("Marszałkowska", result.getStreetName());
        assertEquals("10", result.getBuildingNumber());
        assertEquals("5A", result.getApartmentNumber());
        assertEquals("00-001", result.getZipCode());
        assertEquals("Warszawa", result.getCity());
        assertNotNull(result.getUser());
        assertEquals(1L, result.getUser().getId());

        assertEquals(1L, addressDtoResult.getId());
        assertEquals("Marszałkowska", addressDtoResult.getStreetName());
        assertEquals("10", addressDtoResult.getBuildingNumber());
        assertEquals("5A", addressDtoResult.getApartmentNumber());
        assertEquals("00-001", addressDtoResult.getZipCode());
        assertEquals("Warszawa", addressDtoResult.getCity());
        assertEquals(1L, addressDtoResult.getUserId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenSavingAddressWithNonExistingUser() {
        // given
        AddressDto addressDto = new AddressDto();
        addressDto.setStreetName("Marszałkowska");
        addressDto.setBuildingNumber("10");
        addressDto.setApartmentNumber("5A");
        addressDto.setZipCode("00-001");
        addressDto.setCity("Warszawa");
        addressDto.setUserId(111L);

        Mockito.doThrow(new EntityNotFoundException("User not found")).when(addressDtoMapperMock).map(addressDto);

        //when
        //then
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> addressService.saveAddress(addressDto));
        assertEquals("User not found", exc.getMessage());
        Mockito.verify(addressDtoMapperMock).map(addressDto);
    }

    @Test
    void shouldDeleteAddress() {
        // given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);
        address.setActive(true);

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));

        // when
        addressService.deleteAddress(1L, "tes@email.com");

        // then
        Mockito.verify(addressRepositoryMock).findById(1L);
        Mockito.verify(userRepositoryMock).findByEmail("tes@email.com");
        assertFalse(address.isActive());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAddress() {
        // given
        long nonExistingAddressId = 111L;

        Mockito.when(addressRepositoryMock.findById(nonExistingAddressId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> addressService.deleteAddress(nonExistingAddressId, "tes@email.com"));

        // then
        assertEquals("Address not found", exc.getMessage());
        Mockito.verify(addressRepositoryMock).findById(nonExistingAddressId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingAddressWithNonExistingUser() {
        // given
        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");

        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> addressService.deleteAddress(1L, nonExistingUser));

        // then
        assertEquals("User not found", exc.getMessage());
        Mockito.verify(userRepositoryMock).findByEmail(nonExistingUser);
    }

    @Test
    void shouldThrowExceptionWhenDeleteOtherUserAddress() {
        //given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);

        User otherUser = new User();
        otherUser.setEmail("otherUser@mail.com");

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("otherUser@mail.com")).thenReturn(Optional.of(otherUser));

        //when
        //then
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> addressService.deleteAddress(1L, "otherUser@mail.com"));
        assertEquals("Address belongs to other user", exc.getMessage());
    }

    @Test
    void shouldNotChangeStatusWhenDeletingAlreadyInactiveAddress() {
        // given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);
        address.setActive(false);

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));

        // when
        addressService.deleteAddress(1L, "tes@email.com");

        // then
        Mockito.verify(addressRepositoryMock, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByEmail("tes@email.com");
        assertFalse(address.isActive());
    }

    @Test
    void shouldThrowExceptionWhenUpdateNotExistAddress() {
        // given
        long nonExistingAddressId = 111L;
        String mail = "tes@email.com";
        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();

        Mockito.when(addressRepositoryMock.findById(nonExistingAddressId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> addressService.updateAddress(111L, addressUpdateDto, mail));

        //then
        assertEquals("Address not found", exc.getMessage());
        Mockito.verify(addressRepositoryMock).findById(111L);
    }

    @Test
    void shouldThrowExceptionWhenUpdateAddressWhichNoExistingUser() {
        // given
        Address address = new Address();
        address.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";
        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class, () -> addressService.updateAddress(1L, addressUpdateDto, nonExistingUser));

        //then
        assertEquals("User not found", exc.getMessage());
        Mockito.verify(userRepositoryMock).findByEmail(nonExistingUser);
    }

    @Test
    void shouldThrowExceptionWhenUpdateAddressWhichNotBelongsToUser() {
        // given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        User otherUser = new User();
        otherUser.setEmail("otherUser@mail.com");

        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("otherUser@mail.com")).thenReturn(Optional.of(otherUser));

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () -> addressService.updateAddress(1L, addressUpdateDto, "otherUser@mail.com"));

        //then
        assertEquals("Address belongs to other user, you can not update it", exc.getMessage());
    }


    @Test
    void shouldUpdateAddress() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("tes@email.com");

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);

        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();
        addressUpdateDto.setStreetName("Dluga");
        addressUpdateDto.setBuildingNumber("99");
        addressUpdateDto.setApartmentNumber("11");
        addressUpdateDto.setZipCode("12-123");
        addressUpdateDto.setCity("Kraków");

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));

        //when
        addressService.updateAddress(1L, addressUpdateDto, "tes@email.com");

        //then
        Mockito.verify(addressRepositoryMock).findById(1L);
        Mockito.verify(userRepositoryMock).findByEmail("tes@email.com");

        assertEquals("Dluga", address.getStreetName());
        assertEquals("99", address.getBuildingNumber());
        assertEquals("11", address.getApartmentNumber());
        assertEquals("12-123", address.getZipCode());
        assertEquals("Kraków", address.getCity());
    }

    @Test
    void shouldPartiallyUpdateAddress() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("tes@email.com");

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);

        AddressUpdateDto addressUpdateDto = new AddressUpdateDto();
        addressUpdateDto.setStreetName("Dluga");

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));

        // when
        addressService.updateAddress(1L, addressUpdateDto, "tes@email.com");

        // then
        Mockito.verify(addressRepositoryMock).findById(1L);
        Mockito.verify(userRepositoryMock).findByEmail("tes@email.com");

        assertEquals("Dluga", address.getStreetName());
        assertEquals("10", address.getBuildingNumber());
        assertEquals("5A", address.getApartmentNumber());
        assertEquals("00-001", address.getZipCode());
        assertEquals("Warszawa", address.getCity());
    }

    @Test
    void shouldFindAddressById() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("tes@email.com");

        Address address = new Address();
        address.setId(1L);
        address.setStreetName("Marszałkowska");
        address.setBuildingNumber("10");
        address.setApartmentNumber("5A");
        address.setZipCode("00-001");
        address.setCity("Warszawa");
        address.setUser(user);

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));
        Mockito.when(addressDtoMapperMock.map(address)).thenReturn(new AddressDto("Marszałkowska", "10", "5A", "00-001", "Warszawa", 1L));

        //when
        AddressDto addressDto = addressService.findAddressById(1L, "tes@email.com").orElseThrow();

        //then
        assertEquals(addressDto.getStreetName(), "Marszałkowska");
    }

    @Test
    void shouldThrowExceptionWhenTryGetAddressWhichNotBelongsToUser() {
        // given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        Address address = new Address();
        address.setId(1L);
        address.setUser(user);

        User otherUser = new User();
        otherUser.setEmail("otherUser@mail.com");

        Mockito.when(addressRepositoryMock.findById(1L)).thenReturn(Optional.of(address));
        Mockito.when(userRepositoryMock.findByEmail("otherUser@mail.com")).thenReturn(Optional.of(otherUser));

        //when
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
                () -> addressService.findAddressById(1L, "otherUser@mail.com"));

        //then
        assertEquals("Address belongs to other user, you can not get it", exc.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTryGetNotExistAddress() {
        // given
        User user = new User();
        user.setEmail("tes@email.com");
        user.setId(1L);

        long nonExistingAddressId = 111L;

        Mockito.when(userRepositoryMock.findByEmail("tes@email.com")).thenReturn(Optional.of(user));
        Mockito.when(addressRepositoryMock.findById(nonExistingAddressId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> addressService.findAddressById(111L, "tes@email.com"));

        //then
        assertEquals("Address not found", exc.getMessage());
        Mockito.verify(addressRepositoryMock).findById(111L);
    }

    @Test
    void shouldThrowExceptionWhenTryGetAddressWhenUserNotExist() {
        // given
        Address address = new Address();
        address.setId(1L);
        String nonExistingUser = "nonExistUser@mail.com";

        Mockito.when(userRepositoryMock.findByEmail(nonExistingUser)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exc = assertThrows(EntityNotFoundException.class,
                () -> addressService.findAddressById(1L, nonExistingUser));

        //then
        assertEquals("User not found", exc.getMessage());
        Mockito.verify(userRepositoryMock).findByEmail(nonExistingUser);
    }
}