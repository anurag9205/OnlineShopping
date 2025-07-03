package org.example.onlineshoppingwithreview.repository;

import org.example.onlineshoppingwithreview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductName(String productName);
}
