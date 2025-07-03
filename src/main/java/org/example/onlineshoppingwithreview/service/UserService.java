package org.example.onlineshoppingwithreview.service;

import org.example.onlineshoppingwithreview.model.User;
import org.example.onlineshoppingwithreview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).get();
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public void updateUser(User user) {
        userRepo.save(user);
    }
    public void register(User user) {
        userRepo.save(user);
    }

    public boolean emailExists(String email) {
        return userRepo.existsByEmail(email);
    }
}
