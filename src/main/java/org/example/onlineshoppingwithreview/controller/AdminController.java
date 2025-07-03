package org.example.onlineshoppingwithreview.controller;

import jakarta.servlet.http.HttpSession;
import org.example.onlineshoppingwithreview.model.Category;
import org.example.onlineshoppingwithreview.model.Product;
import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
//    @GetMapping("/login")
//    public String showAdminLogin(@RequestParam(value="error",required=false) String error,
//                                 @RequestParam(value="logout",required=false) String logout,
//                                 Model model) {
//        if (error != null) model.addAttribute("error","Invalid admin credentials.");
//        if (logout != null) model.addAttribute("success","Admin logged out.");
//        return "admin-login"; // admin-login.html
//    }


    @GetMapping("/home")
    public String dashboard(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/login";
        }


        return "admin-dashboard";
    }


    @GetMapping("/add-category")
    public String addCategoryPage() {
        return "add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam String name) {
        adminService.addCategory(name);
        return "redirect:/admin/home";
    }
    @GetMapping("/categories")
    public String viewCategories(Model model) {
        model.addAttribute("categories", adminService.getAllCategories());
        return "view-categories";
    }

    @GetMapping("/category/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        model.addAttribute("category", adminService.getCategoryById(id));
        return "edit-category";
    }

    @PostMapping("/category/update")
    public String updateCategory(@ModelAttribute("category") Category category) {
        adminService.updateCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        adminService.deleteCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/add-product")
    public String addProductPage(Model model) {
        model.addAttribute("product", new Product()); // ✅ Add this line
        model.addAttribute("categories", adminService.getAllCategories());
        return "add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam Long categoryId) {
        adminService.addProductToCategory(product, categoryId);
        return "redirect:/admin/home";
    }
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = adminService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", adminService.getAllCategories());
        return "edit-product";
    }

    @PostMapping("/product/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam Long categoryId) {
        adminService.updateProductWithCategory(product, categoryId);
        return "redirect:/admin/products";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        adminService.deleteProductById(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    public String viewProducts(Model model) {
        model.addAttribute("products", adminService.getAllProducts());
        return "view-products";
    }
}
