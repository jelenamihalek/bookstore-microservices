package com.bookstore.book_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.book_service.models.Book;



public interface BookRepository extends JpaRepository<Book,Integer>{
}
