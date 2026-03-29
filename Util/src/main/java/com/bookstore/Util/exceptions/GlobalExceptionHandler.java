package com.bookstore.Util.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(InvalidRoleAssigmentException.class)
	public ResponseEntity<?> handleInvalidRoleException(InvalidRoleAssigmentException ex){

	    return ResponseEntity.status(HttpStatus.FORBIDDEN)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Role assignment is not allowed",
	                    HttpStatus.FORBIDDEN
	            ));
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFounfException(UserNotFoundException ex){

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "User not found",
	                    HttpStatus.NOT_FOUND
	            ));
	}
	
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<?> handleBookNotFounfException(BookNotFoundException ex){

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "User not found",
	                    HttpStatus.NOT_FOUND
	            ));
	} 
	
	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<?> handleStock(InsufficientStockException ex){

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Not enough stock",
	                    HttpStatus.BAD_REQUEST
	            ));
	}
	
	@ExceptionHandler(BookAlreadyExistsException.class)
	public ResponseEntity<?> handleBookExists(BookAlreadyExistsException ex){

	    return ResponseEntity.status(HttpStatus.CONFLICT)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Book already exists",
	                    HttpStatus.CONFLICT
	            ));
	}
	
	@ExceptionHandler(InvalidBookQuantityException.class)
	public ResponseEntity<?> handleQuantity(InvalidBookQuantityException ex){

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Invalid quantity",
	                    HttpStatus.BAD_REQUEST
	            ));
	}
	@ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionModel(
                        ex.getMessage(),
                        "Unexpected error",
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));
    }
	@ExceptionHandler(MissingFieldException.class)
	public ResponseEntity<?> handleMissingField(MissingFieldException ex) {

	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Missing required field: " + ex.getField(),
	                    HttpStatus.BAD_REQUEST
	            ));
	}
	
	@ExceptionHandler(UserNotFoundByEmailException.class)
	public ResponseEntity<?> handleUserNotFoundByEmail(UserNotFoundByEmailException ex) {

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "User not found with given email",
	                    HttpStatus.NOT_FOUND
	            ));
	}
	
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<?> handleOrderNotFound(OrderNotFoundException ex){
	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(new ExceptionModel(ex.getMessage(), "Order not found", HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(InvalidOrderException.class)
	public ResponseEntity<?> handleInvalidOrder(InvalidOrderException ex){
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ExceptionModel(ex.getMessage(), "Invalid order", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(OrderAccessDeniedException.class)
	public ResponseEntity<?> handleAccessDenied(OrderAccessDeniedException ex){

	    return ResponseEntity.status(HttpStatus.FORBIDDEN)
	            .body(new ExceptionModel(
	                    ex.getMessage(),
	                    "Access denied",
	                    HttpStatus.FORBIDDEN
	            ));
	}
}