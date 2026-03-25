package com.bookstore.userservice.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bookstore.userservice.models.User;
import com.bookstore.userservice.services.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    
 
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/admin")
    public User createUserAsAdmin(@RequestBody User user) {
    	;
        return userService.createUserAsAdmin(user);
    }
    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id,@RequestBody User user) {
    	
    	return userService.updateUser(id, user);
    }
    
    @PutMapping("/admin/{id}")
    public User updateUserRole(@PathVariable int id,
                               @RequestBody User user) {

        return userService.updateUserRole(id, user);
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
    @GetMapping("/email")
    public User getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }
}