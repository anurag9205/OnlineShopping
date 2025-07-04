package org.example.onlineshoppingwithreview.repository;

import org.example.onlineshoppingwithreview.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface   ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryNameIgnoreCase(String categoryName);

    List<Product> findByCategoryId(Long categoryId);
}
