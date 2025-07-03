package org.example.onlineshoppingwithreview.controller;

import org.example.onlineshoppingwithreview.model.Order;
import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.OrderRepository;
import org.example.onlineshoppingwithreview.repository.UserRepository;
import org.example.onlineshoppingwithreview.service.OrderService;
import org.example.onlineshoppingwithreview.service.ProductService;
import org.example.onlineshoppingwithreview.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private ProductService productService;
    @Autowired private OrderService orderService;
    @Autowired private ReviewService reviewService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;


    @GetMapping("/home")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.isAuthenticated() || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return "redirect:/login";
        }

        String email = auth.getName(); // Get the logged-in user's email
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("reviewMap", reviewService.getAllReviewsGroupedByProductId());
        model.addAttribute("selectedCategory", null); // Or your logic
        model.addAttribute("selectedProduct", null);  // Or your logic
        model.addAttribute("showReviews", false);     // Or session-based if needed

        return "user-dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

        List<Product> results = productService.searchByNameOrCategory(query);
        model.addAttribute("products", results);
        model.addAttribute("query", query);
        return "search-results";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        Product product = productService.getProductById(productId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);


        Order item = new Order();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setOrderDate(LocalDate.now());
        item.setDate(new Date());
        item.setPaid(false);

        cart.add(item);
        session.setAttribute("cart", cart);
        return "redirect:/user/cart";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        model.addAttribute("cart", cart == null ? new ArrayList<>() : cart);
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam int index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index);
            session.setAttribute("cart", cart);
        }
        return "redirect:/user/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        if (cart != null) {
            for (Order item : cart) {
                orderService.placeOrder(item);
            }
            session.removeAttribute("cart");
        }
        return "redirect:/user/orders";
    }

    @GetMapping("/orders")
    public String viewOrders(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        List<Order> orders = orderService.getOrdersByUser(user.getId());

        for (Order order : orders) {
            if (order.getDate() == null) order.setDate(new Date());

            LocalDateTime orderDateTime = order.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            boolean eligible = ChronoUnit.HOURS.between(orderDateTime, LocalDateTime.now()) <= 24;
            order.setCancelEligible(!order.isPaid() && eligible);
        }

        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/category/{id}")
    public String viewProductsByCategory(@PathVariable Long id, HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

        model.addAttribute("products", productService.getProductsByCategoryId(id));
        model.addAttribute("category", productService.getCategoryById(id).getName());
        model.addAttribute("reviewMap", reviewService.getAllReviewsGroupedByProductId());
        model.addAttribute("showReviews", session.getAttribute("showReviews") != null ? session.getAttribute("showReviews") : false);

        return "category-products";
    }

    @PostMapping("/cancel-order")
    public String cancelOrder(@RequestParam Long orderId, HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return "redirect:/login";

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getUser().getId().equals(user.getId()) && orderService.canCancelOrder(order)) {
            orderService.cancelOrder(orderId);
            model.addAttribute("message", "Order cancelled successfully.");
        } else {
            model.addAttribute("message", "Cannot cancel after 24 hours or access denied.");
        }

        model.addAttribute("orders", orderService.getOrdersByUser(user.getId()));
        return "orders";
    }

    @PostMapping("/pay-order")
    public String payOrder(@RequestParam Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaid(true);
            orderRepository.save(order);
        }
        return "redirect:/user/orders";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/user/cart";

        double total = cart.stream()
                .filter(o -> !o.isPaid())
                .mapToDouble(o -> o.getProduct().getPrice() * o.getQuantity())
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        return "checkout";
    }

    @PostMapping("/pay-cart")
    public String payCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Order> cart = (List<Order>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/user/cart";

        double totalPaid = 0;
        for (Order item : cart) {
            if (!item.isPaid()) {
                item.setPaid(true);
                orderService.placeOrder(item);
                totalPaid += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        session.removeAttribute("cart");
        model.addAttribute("totalAmount", totalPaid);
        return "payment-success";
    }

    @PostMapping("/toggle-reviews")
    public String toggleReviews(HttpSession session) {
        session.setAttribute("showReviews", true);
        return "redirect:/user/reviews-page";
    }

    @GetMapping("/reviews-page")
    public String reviewsPage(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !"USER".equalsIgnoreCase(user.getRole())) return "redirect:/login";

        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("reviewMap", reviewService.getAllReviewsGroupedByProductId());
        model.addAttribute("selectedCategory", session.getAttribute("selectedCategory"));
        model.addAttribute("selectedProduct", session.getAttribute("selectedProduct"));
        model.addAttribute("loggedInUser", session.getAttribute("user"));

        return "reviews-page";
    }

    @PostMapping("/reviews-filter")
    public String filterReviewPage(@RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) Long productId,
                                   HttpSession session) {
        session.setAttribute("selectedCategory", categoryId);
        session.setAttribute("selectedProduct", productId);
        return "redirect:/user/reviews-page";
    }

    @PostMapping("/review")
    public String submitReview(@RequestParam Long productId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !email.equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }

        Product product = productService.getProductById(productId);
        reviewService.addReview(user, product, rating, comment);

        return "redirect:/user/category/" + product.getCategory().getId();
    }
    @PostMapping("/user/delete-review")
    public String deleteReview(@RequestParam Long reviewId, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user != null) {
            reviewService.deleteReviewByIdAndUser(reviewId, user.getId());
        }
        return "redirect:/user/home"; // or wherever your review list reloads from
    }



}
