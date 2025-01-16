package com.example.apiecommerce.domain.address;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteAddress(long id){
        if (!addressRepository.existsById(id)){
            throw new EntityNotFoundException("Address not found");
        }
        addressRepository.deleteById(id);
    }




}
