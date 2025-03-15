package com.example.apiecommerce.domain.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {
    List<Order> findAllByUserId(long userId);
}
