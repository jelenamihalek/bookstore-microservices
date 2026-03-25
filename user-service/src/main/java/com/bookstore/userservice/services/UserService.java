package com.bookstore.userservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.userservice.models.User;
import com.bookstore.userservice.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @PostConstruct
    public void initAdmin() {
        if(userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
    
    // 👤 REGISTER (PUBLIC)
    public User register(User user) {

        if (user.getRole() != null) {
            throw new RuntimeException("Cannot assign role");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    // 👑 ADMIN CREATE USER
    public User createUserAsAdmin(User user) {

       

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // admin može birati role
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {

        

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public User updateUser(int id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null) {
            throw new RuntimeException("Role cannot be changed here");
        }

        return userRepository.save(user);
    }
    
    public User updateUserRole(int id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatedUser.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(int id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}