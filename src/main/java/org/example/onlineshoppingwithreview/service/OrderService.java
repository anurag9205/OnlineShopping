package org.example.onlineshoppingwithreview.service;

import org.example.onlineshoppingwithreview.model.Order;
import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.OrderRepository;
import org.example.onlineshoppingwithreview.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepo;
    @Autowired private ProductRepository productRepo;

    public void placeOrder(User user, Long productId, int quantity, boolean paid) {
        Product product = productRepo.findById(productId).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setPaid(paid);
        order.setOrderDate(LocalDate.now());
        order.setDate(new Date());
        orderRepo.save(order);
    }

    public void placeOrder(User user, Long productId, int quantity) {
        placeOrder(user, productId, quantity, true);
    }

    public void placeOrder(Order order) {
        if (order.getDate() == null) order.setDate(new Date());
        if (order.getOrderDate() == null) order.setOrderDate(LocalDate.now());
        orderRepo.save(order);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    public boolean canCancelOrder(Order order) {
        if (order.getDate() == null) return false;
        return Instant.ofEpochMilli(order.getDate().getTime())
                .plus(24, ChronoUnit.HOURS)
                .isAfter(Instant.now());
    }

    public void cancelOrder(Long orderId) {
        orderRepo.deleteById(orderId);
    }
}
