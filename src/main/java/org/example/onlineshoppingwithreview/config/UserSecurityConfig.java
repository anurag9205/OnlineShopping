package org.example.onlineshoppingwithreview.config;

import org.example.onlineshoppingwithreview.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)
public class UserSecurityConfig {



    @Bean
    public SecurityFilterChain userSecurityFilter(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/login", "/register", "/user/**","/logout", "/style/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/style/**", "/register", "/login").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/user/home", true)
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(e -> e.accessDeniedPage("/unauthorized"));

        return http.build();
    }


}
