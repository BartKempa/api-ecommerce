package com.example.apiecommerce.domain.address;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.address.dto.AddressUpdateDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressDtoMapper addressDtoMapper;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, AddressDtoMapper addressDtoMapper, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.addressDtoMapper = addressDtoMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public AddressDto saveAddress(AddressDto addressDto){
        Address addressToSave = addressDtoMapper.map(addressDto);
        Address savedAddress = addressRepository.save(addressToSave);
        return addressDtoMapper.map(savedAddress);
    }

    @Transactional
    public void deleteAddress(long id, String userMail){
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!address.getUser().equals(user)) {
            throw new IllegalArgumentException("Address belongs to other user");
        }
        address.setActive(false);
    }
    @Transactional
    public void updateAddress(long addressId, AddressUpdateDto addressUpdateDto, String userMail){
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!address.getUser().equals(user)) {
            throw new IllegalArgumentException("Address belongs to other user, you can not update it");
        }
        if (addressUpdateDto.getStreetName() != null){
            address.setStreetName(addressUpdateDto.getStreetName());
        }
        if (addressUpdateDto.getBuildingNumber() != null){
            address.setBuildingNumber(addressUpdateDto.getBuildingNumber());
        }
        if (addressUpdateDto.getApartmentNumber() != null){
            address.setApartmentNumber(addressUpdateDto.getApartmentNumber());
        }
        if (addressUpdateDto.getZipCode() != null){
            address.setZipCode(addressUpdateDto.getZipCode());
        }
        if (addressUpdateDto.getCity() != null){
            address.setCity(addressUpdateDto.getCity());
        }
    }

    public Optional<AddressDto> findAddressById(long addressId, String userMail){
        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        if (!address.getUser().equals(user)) {
            throw new IllegalArgumentException("Address belongs to other user, you can not get it");
        }
        return Optional.of(addressDtoMapper.map(address));
    }
}
