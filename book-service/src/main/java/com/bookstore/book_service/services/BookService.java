package com.bookstore.book_service.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.book_service.models.Book;
import com.bookstore.book_service.repositories.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	public BookService(BookRepository bookRepository) {
		this.bookRepository=bookRepository;
	}
	
	public List<Book> getAllBooks(){
		return bookRepository.findAll();
	}
	 public Book getBookById(int id) {
	        return bookRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Book not found"));
	    }
	
	public Book createBook (Book book) {
		List<Book>books=bookRepository.findAll();
		for (Book bookModel : books) {
			if (bookModel.getTitle().equals(book.getTitle())) {
                throw new RuntimeException("Book already exists");
            }
				break;
		}
		return bookRepository.save(book);
		
	}
	public Book updateBook(int id, Book updatedBook) {

	    Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	    book.setTitle(updatedBook.getTitle());
	    book.setAuthor(updatedBook.getAuthor());
	    book.setPrice(updatedBook.getPrice());
	    book.setStock(updatedBook.getStock());

	    return bookRepository.save(book);
	}
	
	public void deleteBook(int id) {
		  Book book = bookRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Book not found"));
		bookRepository.delete(book);
	}
	
	public void decreaseStock(int id, int quantity) {

	    Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	    if (book.getStock() < quantity) {
	        throw new RuntimeException("Not enough stock");
	    }

	    book.setStock(book.getStock() - quantity);

	    bookRepository.save(book);
	}
	
	public void increaseStock(int id, int quantity) {

	    Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	    book.setStock(book.getStock() + quantity);

	    bookRepository.save(book);
	}
}
