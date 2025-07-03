package org.example.onlineshoppingwithreview.config;

import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeederConfig {

    @Bean
    public CommandLineRunner seedAdminData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Check if admin already exists
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {

                // Create admin user
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("Admin@123")); // encode password
                admin.setRole("ADMIN");

                // Save admin user
                userRepository.save(admin);

                System.out.println("✅ Admin user created successfully!");
            } else {
                System.out.println("ℹ️ Admin user already exists.");
            }
        };
    }
}
