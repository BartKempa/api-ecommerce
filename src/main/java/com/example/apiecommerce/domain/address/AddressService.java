package com.example.apiecommerce.domain.address;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressDtoMapper addressDtoMapper;

    public AddressService(AddressRepository addressRepository, AddressDtoMapper addressDtoMapper) {
        this.addressRepository = addressRepository;
        this.addressDtoMapper = addressDtoMapper;
    }

    @Transactional
    public AddressDto saveAddress(AddressDto addressDto){
        Address addressToSave = addressDtoMapper.map(addressDto);
        Address savedAddress = addressRepository.save(addressToSave);
        return addressDtoMapper.map(savedAddress);
    }




}
