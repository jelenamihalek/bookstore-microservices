package com.bookstore.book_service.service;

import com.bookstore.book_service.models.Book;
import com.bookstore.book_service.repositories.BookRepository;
import com.bookstore.book_service.services.BookService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    //  CREATE 
    @Test
    void shouldCreateBook() {
        Book book = new Book(0, "Title", "Author", 10, 5);

        when(bookRepository.findAll()).thenReturn(List.of());
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.createBook(book);

        assertEquals("Title", result.getTitle());
    }

    //  TITLE MISSING
    @Test
    void shouldThrow_whenTitleMissing() {
        Book book = new Book(0, null, "Author", 10, 5);

        assertThrows(RuntimeException.class,
                () -> bookService.createBook(book));
    }

    //  DUPLICATE BOOK
    @Test
    void shouldThrow_whenBookExists() {
        Book existing = new Book(1, "Title", "Author", 10, 5);
        Book newBook = new Book(0, "Title", "Author2", 20, 3);

        when(bookRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(RuntimeException.class,
                () -> bookService.createBook(newBook));
    }

    //  GET BY ID
    @Test
    void shouldGetBookById() {
        Book book = new Book(1, "T", "A", 10, 5);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1);

        assertEquals(1, result.getId());
    }

    //  BOOK NOT FOUND
    @Test
    void shouldThrow_whenBookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> bookService.getBookById(1));
    }

    //  UPDATE
    @Test
    void shouldUpdateBook() {
        Book existing = new Book(1, "Old", "A", 10, 5);
        Book updated = new Book(0, "New", "B", 20, 10);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any())).thenReturn(existing);

        Book result = bookService.updateBook(1, updated);

        assertEquals("New", result.getTitle());
    }

    //  INVALID PRICE
    @Test
    void shouldThrow_whenPriceInvalid() {
        Book existing = new Book(1, "Old", "A", 10, 5);
        Book updated = new Book(0, "New", "B", 0, 10);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class,
                () -> bookService.updateBook(1, updated));
    }

    //  DELETE
    @Test
    void shouldDeleteBook() {
        Book book = new Book(1, "T", "A", 10, 5);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.deleteBook(1);

        verify(bookRepository).delete(book);
    }

    //  DECREASE STOCK
    @Test
    void shouldDecreaseStock() {
        Book book = new Book(1, "T", "A", 10, 10);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        bookService.decreaseStock(1, 5);

        assertEquals(5, book.getStock());
        verify(bookRepository).save(book);
    }

    //  INVALID QUANTITY
    @Test
    void shouldThrow_whenQuantityInvalid() {
        assertThrows(RuntimeException.class,
                () -> bookService.decreaseStock(1, 0));
    }

    //  NOT ENOUGH STOCK
    @Test
    void shouldThrow_whenNotEnoughStock() {
        Book book = new Book(1, "T", "A", 10, 2);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThrows(RuntimeException.class,
                () -> bookService.decreaseStock(1, 5));
    }
}