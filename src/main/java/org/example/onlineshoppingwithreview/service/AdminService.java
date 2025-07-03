package org.example.onlineshoppingwithreview.service;

import org.example.onlineshoppingwithreview.model.Category;
import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.repository.CategoryRepository;
import org.example.onlineshoppingwithreview.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired private CategoryRepository categoryRepo;
    @Autowired private ProductRepository productRepo;

    public void addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryRepo.save(category);
    }

    public void addProductToCategory(Product product, Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        product.setCategory(category);
        productRepo.save(product);
    }
    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id).orElseThrow();
    }

    public void updateCategory(Category category) {
        categoryRepo.save(category);
    }

    public void deleteCategoryById(Long id) {
        categoryRepo.deleteById(id);
    }
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow();
    }

    public void updateProductWithCategory(Product product, Long categoryId) {
        product.setCategory(categoryRepo.findById(categoryId).orElseThrow());
        productRepo.save(product);
    }

    public void deleteProductById(Long id) {
        productRepo.deleteById(id);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }
}
