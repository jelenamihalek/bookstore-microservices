package com.bookstore.userservice.controller;

import com.bookstore.userservice.models.User;
import com.bookstore.userservice.services.UserService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {

        User user = new User("test@gmail.com", "123", "USER");

        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "test@gmail.com",
                        "password": "123"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));
    }
    
    @Test
    void shouldGetUserById() throws Exception {
        User user = new User(1, "mail", "pass", "USER");

        when(userService.getUserById(1)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail"));
    }
    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User successfully deleted"));
    }
    
    @Test
    void shouldRegisterUser() throws Exception {
        User user = new User("mail", "pass", "USER");

        when(userService.register(any())).thenReturn(user);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "email": "mail",
                        "password": "pass"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mail"));
    }
}