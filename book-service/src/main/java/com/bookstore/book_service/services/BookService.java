package com.bookstore.book_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.Util.exceptions.BookAlreadyExistsException;
import com.bookstore.Util.exceptions.BookNotFoundException;
import com.bookstore.Util.exceptions.InsufficientStockException;
import com.bookstore.Util.exceptions.InvalidBookQuantityException;
import com.bookstore.Util.exceptions.MissingFieldException;
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
	                .orElseThrow(() -> new BookNotFoundException("Book not found",id));
	    }
	
	public Book createBook (Book book) {
		
if (book.getTitle() == null || book.getTitle().isEmpty()) {
		        throw new MissingFieldException("Title is required", "title");
		    }

		    if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
		        throw new MissingFieldException("Author is required", "author");
		    }

		    if (book.getPrice() <= 0) {
		        throw new MissingFieldException("Price must be greater than 0", "price");
		    }

		    if (book.getStock() < 0) {
		        throw new MissingFieldException("Stock cannot be negative", "stock");
		    }

		    List<Book> books = bookRepository.findAll();
		    for (Book bookModel : books) {
		        if (bookModel.getTitle().equals(book.getTitle())) {
		            throw new BookAlreadyExistsException(
		                "Book already exists with title: " + book.getTitle(),
		                book.getTitle()
		            );
		        }
		    }

		    return bookRepository.save(book);
		
	}
	public Book updateBook(int id, Book updatedBook) {

	    Book book = bookRepository.findById(id)
	            .orElseThrow(() -> new BookNotFoundException("Book not found", id));

	    if (updatedBook.getTitle() == null || updatedBook.getTitle().isEmpty()) {
	        throw new MissingFieldException("Title is required", "title");
	    }

	    if (updatedBook.getAuthor() == null || updatedBook.getAuthor().isEmpty()) {
	        throw new MissingFieldException("Author is required", "author");
	    }

	    if (updatedBook.getPrice() <= 0) {
	        throw new MissingFieldException("Price must be greater than 0", "price");
	    }

	    if (updatedBook.getStock() < 0) {
	        throw new MissingFieldException("Stock cannot be negative", "stock");
	    }

	    book.setTitle(updatedBook.getTitle());
	    book.setAuthor(updatedBook.getAuthor());
	    book.setPrice(updatedBook.getPrice());
	    book.setStock(updatedBook.getStock());

	    return bookRepository.save(book);
	}
	
	public void deleteBook(int id) {
		  Book book = bookRepository.findById(id)
	                .orElseThrow(() -> new BookNotFoundException("Book not found",id));
		bookRepository.delete(book);
	}
	
	public void decreaseStock(int id, int quantity) {

		if (quantity <= 0) {
		    throw new InvalidBookQuantityException("Quantity must be greater than 0", quantity);
		}
	    Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found",id));

	    if (book.getStock() < quantity) {
	        throw new InsufficientStockException(
	            "Not enough stock",
	            quantity,
	            book.getStock()
	        );
	    }

	    book.setStock(book.getStock() - quantity);

	    bookRepository.save(book);
	}
	
	public void increaseStock(int id, int quantity) {

	    Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found",id));

	    book.setStock(book.getStock() + quantity);

	    bookRepository.save(book);
	}
}
