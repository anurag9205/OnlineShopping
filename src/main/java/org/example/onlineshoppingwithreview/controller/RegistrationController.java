package org.example.onlineshoppingwithreview.controller;

import jakarta.servlet.http.HttpSession;
import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.UserRepository;
import org.example.onlineshoppingwithreview.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // Initial registration form
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    // Step 1: user submits name, email, password → send OTP
    @PostMapping("/initiate-verification")
    public String initiateVerification(@RequestParam String name,
                                       @RequestParam String email,
                                       @RequestParam String password,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (userRepo.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "This email is already registered.");
            return "redirect:/register";
        }

        // Store user data in session
        session.setAttribute("pendingName", name);
        session.setAttribute("pendingEmail", email);
        session.setAttribute("pendingPassword", password);

        // Generate + send OTP
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        session.setAttribute("otpCode", code);

        emailService.sendSimpleMessage(
                email,
                "Email Verification Code",
                "Your OTP is: " + code
        );

        redirectAttributes.addFlashAttribute("success", "OTP sent to your email.");
        return "redirect:/verify-otp";
    }

    // Step 2: show OTP page
    @GetMapping("/verify-otp")
    public String showOtpPage() {
        return "verify-otp";
    }

    // Step 3: verify OTP and complete registration
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        String sessionOtp = (String) session.getAttribute("otpCode");
        String name = (String) session.getAttribute("pendingName");
        String email = (String) session.getAttribute("pendingEmail");
        String password = (String) session.getAttribute("pendingPassword");

        if (sessionOtp != null && sessionOtp.equals(otp)
                && name != null && email != null && password != null) {

            // Save new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("USER");
            userRepo.save(user);

            // Clear session data
            session.removeAttribute("pendingName");
            session.removeAttribute("pendingEmail");
            session.removeAttribute("pendingPassword");
            session.removeAttribute("otpCode");

            redirectAttributes.addFlashAttribute("success", "Registration complete! Please login.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP or session expired. Try again.");
            return "redirect:/verify-otp";
        }
    }
}
