package com.bookstore.book_service.controller;

import com.bookstore.book_service.controllers.BookController;
import com.bookstore.book_service.models.Book;
import com.bookstore.book_service.services.BookService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void shouldCreateBook() throws Exception {
        Book book = new Book(1, "Title", "Author", 10, 5);

        when(bookService.createBook(any())).thenReturn(book);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Title",
                        "author": "Author",
                        "price": 10,
                        "stock": 5
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }
}