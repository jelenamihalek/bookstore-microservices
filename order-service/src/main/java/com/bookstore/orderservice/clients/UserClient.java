package com.bookstore.orderservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.service_library.dtos.UserDTO;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{id}")
    Object getUserById(@PathVariable int id);
    
    @GetMapping("/users/email")
	UserDTO findByEmail(@RequestParam(value="email") String email);
}