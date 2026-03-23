package com.bookstore.orderservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.bookstore.orderservice.dtos.BookDTO;

@FeignClient(name = "book-service")
public interface BookClient {

    @GetMapping("/books/{id}")
    BookDTO getBookById(@PathVariable int id);

    @PutMapping("/books/{id}/decrease")
    void decreaseStock(@PathVariable int id, @RequestParam int quantity);
    @PutMapping("/books/{id}/increase")
    void increaseStock(@PathVariable int id, @RequestParam int quantity);
}