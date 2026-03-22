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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.book_service.models.Book;
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
    public List<Book> getAllBooks(){
        return bookService.getAllBooks();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Book getBook(@PathVariable int id){
        return bookService.getBookById(id);
    }

    // CREATE
    @PostMapping
    public Book createBook(@RequestBody Book book){
        return bookService.createBook(book);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable int id,
                                @RequestBody Book book){
        return bookService.updateBook(id, book);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id){
        bookService.deleteBook(id);
    }
    
    @PutMapping("/{id}/decrease")
    public void decreaseStock(@PathVariable int id,
                              @RequestParam int quantity) {

        bookService.decreaseStock(id, quantity);
    }
}
