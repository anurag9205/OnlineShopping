package org.example.onlineshoppingwithreview.service;

import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.model.Review;
import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.ProductRepository;
import org.example.onlineshoppingwithreview.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired private ReviewRepository reviewRepo;
    @Autowired private ProductRepository productRepo;

    public void addReview(User user, Product product, int rating, String comment) {
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        reviewRepo.save(review);
    }
    public void deleteReviewByIdAndUser(Long reviewId, Long userId) {
        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review != null && review.getUser().getId().equals(userId)) {
            reviewRepo.delete(review);
        }
    }




    public List<Review> getReviewsByProduct(String productName) {
        return reviewRepo.findByProductName(productName);
    }
    public Map<Long, List<Review>> getAllReviewsGroupedByProductId() {
        List<Review> allReviews = reviewRepo.findAll();
        return allReviews.stream().collect(Collectors.groupingBy(r -> r.getProduct().getId()));
    }
}
