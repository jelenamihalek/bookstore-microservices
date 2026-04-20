package com.bookstore.userservice.service;

import com.bookstore.Util.exceptions.MissingFieldException;
import com.bookstore.userservice.models.User;
import com.bookstore.userservice.repositories.UserRepository;
import com.bookstore.userservice.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ✅ REGISTER
    @Test
    void shouldRegisterUserSuccessfully() {
        User user = new User("test@gmail.com", "123", null);

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(user);

        assertEquals("USER", result.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowException_whenRoleIsProvidedOnRegister() {
        User user = new User("test@gmail.com", "123", "ADMIN");

        assertThrows(RuntimeException.class, () -> userService.register(user));
    }

    // ✅ CREATE USER
    @Test
    void shouldCreateUserSuccessfully() {
        User user = new User("test@gmail.com", "123", null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("USER", result.getRole());
    }

    @Test
    void shouldThrowException_whenEmailExists() {
        User user = new User("test@gmail.com", "123", null);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.createUser(user));
    }

    // ✅ GET USER BY ID
    @Test
    void shouldReturnUserById() {
        User user = new User(1, "mail", "pass", "USER");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void shouldThrow_whenUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1));
    }

    // ✅ DELETE USER
    @Test
    void shouldDeleteUser() {
        User user = new User(1, "mail", "pass", "USER");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldNotDeleteLastAdmin() {
        User admin = new User(1, "admin", "pass", "ADMIN");

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.countByRole("ADMIN")).thenReturn(1L);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1));
    }
    
    @Test
    void shouldThrow_whenEmailIsMissing() {
        User existing = new User(1, "mail", "pass", "USER");
        User updated = new User(null, "pass", null);

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(MissingFieldException.class,
                () -> userService.updateUser(1, updated));
    }
    
    @Test
    void shouldThrow_whenEmailNotFound() {
        when(userRepository.findByEmail("x@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail("x@gmail.com"));
    }
    
    @Test
    void shouldThrow_whenPasswordMissing() {
        User user = new User("mail", null, null);

        assertThrows(MissingFieldException.class,
                () -> userService.createUserAsAdmin(user));
    }
}