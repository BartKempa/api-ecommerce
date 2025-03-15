package com.example.apiecommerce.domain.address;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Long> {
    List<Address> findAllByUserId(long userId);
}
