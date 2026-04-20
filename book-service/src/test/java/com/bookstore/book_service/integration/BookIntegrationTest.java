package com.bookstore.book_service.integration;

import com.bookstore.book_service.models.Book;
import com.bookstore.book_service.repositories.BookRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll(); // čisti bazu pre svakog testa
    }

    // ✅ CREATE + GET ALL
    @Test
    void shouldCreateAndGetBooks() throws Exception {

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Clean Code",
                        "author": "Robert Martin",
                        "price": 50,
                        "stock": 10
                    }
                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    // ✅ GET BY ID
    @Test
    void shouldGetBookById() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Java", "Oracle", 20, 5)
        );

        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java"));
    }

    // ❌ GET BY ID - NOT FOUND
    @Test
    void shouldReturn404_whenBookNotFound() throws Exception {

        mockMvc.perform(get("/books/999"))
                .andExpect(status().is4xxClientError());
    }

    // ✅ UPDATE
    @Test
    void shouldUpdateBook() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Old", "A", 10, 5)
        );

        mockMvc.perform(put("/books/" + book.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "New",
                        "author": "B",
                        "price": 20,
                        "stock": 10
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"));
    }

    // ✅ DELETE
    @Test
    void shouldDeleteBook() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Delete", "A", 10, 5)
        );

        mockMvc.perform(delete("/books/" + book.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(status().is4xxClientError());
    }

    // ✅ DECREASE STOCK
    @Test
    void shouldDecreaseStock() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Stock", "A", 10, 10)
        );

        mockMvc.perform(put("/books/" + book.getId() + "/decrease?quantity=5"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books/" + book.getId()))
                .andExpect(jsonPath("$.stock").value(5));
    }

    // ❌ NOT ENOUGH STOCK
    @Test
    void shouldFail_whenNotEnoughStock() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Stock", "A", 10, 2)
        );

        mockMvc.perform(put("/books/" + book.getId() + "/decrease?quantity=5"))
                .andExpect(status().is4xxClientError());
    }

    // ❌ INVALID QUANTITY
    @Test
    void shouldFail_whenQuantityInvalid() throws Exception {

        Book book = bookRepository.save(
                new Book(0, "Stock", "A", 10, 10)
        );

        mockMvc.perform(put("/books/" + book.getId() + "/decrease?quantity=0"))
                .andExpect(status().is4xxClientError());
    }
}