package com.bookstore.userservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookstore.userservice.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}