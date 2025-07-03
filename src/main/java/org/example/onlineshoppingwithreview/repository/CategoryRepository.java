package org.example.onlineshoppingwithreview.repository;

import org.example.onlineshoppingwithreview.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByNameIgnoreCase(String name);
}
