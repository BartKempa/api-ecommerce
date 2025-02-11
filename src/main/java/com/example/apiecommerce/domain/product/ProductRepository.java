package com.example.apiecommerce.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
    Page<Product> findAllByCategory_CategoryNameIgnoreCase(String categoryName, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.cartItems ci WHERE ci.id = :id")
    Optional<Product> getProductByCartItemId(@Param("id") long id);
}
