package org.example.onlineshoppingwithreview.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String userLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpServletRequest request,
                                HttpSession session,
                                Model model) {
        if (error != null) model.addAttribute("error", "Invalid credentials.");
        if (logout != null) model.addAttribute("success", "Logged out.");
        session.setAttribute("loginReferer", request.getRequestURI());
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "logout", required = false) String logout,
                                 HttpServletRequest request,
                                 HttpSession session,
                                 Model model) {
        if (error != null) model.addAttribute("error", "Invalid admin credentials.");
        if (logout != null) model.addAttribute("success", "Admin logged out.");
        session.setAttribute("loginReferer", request.getRequestURI());
        return "admin-login";
    }
}


