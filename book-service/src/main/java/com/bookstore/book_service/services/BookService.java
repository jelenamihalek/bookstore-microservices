package com.bookstore.book_service.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.bookstore.book_service.models.BookModel;
import com.bookstore.book_service.repositories.BookRepository;

@Service
public class BookService {

	@Autowired
	private BookRepository bookRepository;
	
	public BookService(BookRepository bookRepository) {
		this.bookRepository=bookRepository;
	}
	
	public List<BookModel> getAllBooks(){
		return bookRepository.findAll();
	}
	 public BookModel getBookById(int id) {
	        return bookRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Book not found"));
	    }
	
	public BookModel createBook (BookModel book) {
		List<BookModel>books=bookRepository.findAll();
		for (BookModel bookModel : books) {
			if (bookModel.getTitle().equals(book.getTitle())) {
                throw new RuntimeException("Book already exists");
            }
				break;
		}
		return bookRepository.save(book);
		
	}
	public BookModel updateBook(int id, BookModel updatedBook) {

	    BookModel book = bookRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	    book.setTitle(updatedBook.getTitle());
	    book.setAuthor(updatedBook.getAuthor());
	    book.setPrice(updatedBook.getPrice());
	    book.setStock(updatedBook.getStock());

	    return bookRepository.save(book);
	}
	
	public void deleteBook(int id) {
		  BookModel book = bookRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Book not found"));
		bookRepository.delete(book);
	}
}
