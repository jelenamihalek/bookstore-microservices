package com.bookstore.orderservice.dtos;


public class BookDTO {

	private int id;
	private String title;
	private int stock;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public BookDTO(int id, String title, int stock) {
		super();
		this.id = id;
		this.title = title;
		this.stock = stock;
	}
	public BookDTO() {
		super();
	}
	 
	 
	
}
