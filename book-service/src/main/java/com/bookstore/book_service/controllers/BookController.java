package com.bookstore.book_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.book_service.models.BookModel;
import com.bookstore.book_service.services.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET ALL
    @GetMapping
    public List<BookModel> getAllBooks(){
        return bookService.getAllBooks();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public BookModel getBook(@PathVariable int id){
        return bookService.getBookById(id);
    }

    // CREATE
    @PostMapping
    public BookModel createBook(@RequestBody BookModel book){
        return bookService.createBook(book);
    }

    // UPDATE
    @PutMapping("/{id}")
    public BookModel updateBook(@PathVariable int id,
                                @RequestBody BookModel book){
        return bookService.updateBook(id, book);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id){
        bookService.deleteBook(id);
    }
}
