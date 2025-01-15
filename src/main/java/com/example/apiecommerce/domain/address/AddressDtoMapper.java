package com.example.apiecommerce.domain.address;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AddressDtoMapper {
    private final UserRepository userRepository;

    public AddressDtoMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AddressDto map(Address address){
        if (address == null){
            return null;
        }
        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setStreetName(address.getStreetName());
        addressDto.setBuildingNumber(address.getBuildingNumber());
        addressDto.setApartmentNumber(address.getApartmentNumber());
        addressDto.setZipCode(address.getZipCode());
        addressDto.setCity(address.getCity());
        addressDto.setUserId(address.getUser().getId());
        return addressDto;
    }

    public Address map(AddressDto addressDto){
        if (addressDto == null){
            return null;
        }
        Address address = new Address();
        address.setStreetName(addressDto.getStreetName());
        address.setBuildingNumber(addressDto.getBuildingNumber());
        address.setApartmentNumber(addressDto.getApartmentNumber());
        address.setZipCode(addressDto.getZipCode());
        address.setCity(addressDto.getCity());
        User user = userRepository.findById(addressDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        address.setUser(user);
        return address;
    }
}
