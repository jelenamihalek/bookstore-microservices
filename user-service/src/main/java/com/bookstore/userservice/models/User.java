package com.bookstore.userservice.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(nullable = false, unique=true)
	private String email;
	
	@Column(nullable= false)
	private String password;
	
	
	@Column(nullable = false, columnDefinition="VARCHAR(10) CHECK(role IN ('ADMIN','USER','OWNER'))")
	private String role;
	
	public User() {
	
	}
	public User(String email, String password, String role) {
		super();
		
		this.email = email;
		this.password = password;
		this.role = role;
	}
	public User(int id, String email, String password, String role) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	
}