package org.example.onlineshoppingwithreview.service;

import org.example.onlineshoppingwithreview.model.Category;
import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.repository.CategoryRepository;
import org.example.onlineshoppingwithreview.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository; // ✅ MISSING INJECTION FIXED

    public List<Product> searchByNameOrCategory(String query) {
        List<Product> byName = productRepository.findByNameContainingIgnoreCase(query);
        List<Product> byCategory = productRepository.findByCategoryNameIgnoreCase(query);
        byCategory.removeAll(byName); // avoid duplicates
        byName.addAll(byCategory);
        return byName;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
}
