package com.bookstore.userservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.Util.exceptions.InvalidRoleAssigmentException;
import com.bookstore.Util.exceptions.MissingFieldException;
import com.bookstore.Util.exceptions.UserNotFoundByEmailException;
import com.bookstore.Util.exceptions.UserNotFoundException;
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
    
    public User register(User user) {

    	if (user.getRole() != null) {
    	    throw new InvalidRoleAssigmentException(
    	        "User cannot assign role during registration",
    	        user.getRole()
    	    );
    	}
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public User createUserAsAdmin(User user) {
    	
    	if (user.getEmail() == null) {
    	    throw new MissingFieldException("Email is required", "email");
    	}

    	if (user.getPassword() == null) {
    	    throw new MissingFieldException("Password is required", "password");
    	}

        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
                .orElseThrow(() -> new UserNotFoundException("User not found",id));
    }

    public User createUser(User user) {
    	
    	if (user.getEmail() == null) {
    	    throw new MissingFieldException("Email is required", "email");
    	}

    	if (user.getPassword() == null) {
    	    throw new MissingFieldException("Password is required", "password");
    	}
    	
    	if (userRepository.findByEmail(user.getEmail()).isPresent()) {
    	    throw new RuntimeException("Email already exists");
    	}

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public User updateUser(int id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found",id));
        
        if (updatedUser.getEmail() == null) {
            throw new MissingFieldException("Email is required", "email");
        }

        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null) {
            throw new InvalidRoleAssigmentException("Role cannot be changed here",user.getRole());
        }

        return userRepository.save(user);
    }
    
    public User updateUserRole(int id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found",id));

        if (updatedUser.getRole() == null) {
            throw new MissingFieldException("Role is required", "role");
        }

        user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(int id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found",id));
        
        if (user.getRole().equals("ADMIN")) {

            long adminCount = userRepository.countByRole("ADMIN");

            if (adminCount <= 1) {
                throw new RuntimeException("Cannot delete the last admin");
            }
        }

        userRepository.delete(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
        		.orElseThrow(() -> new UserNotFoundByEmailException(
                        "User not found with email: " + email,
                        email
                ));
    }
}