package com.bookstore.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.bookstore.service_library.clients")

@ComponentScan(basePackages = {
	    "com.bookstore.paymentservice",
	    "com.bookstore.Util",
	    "com.bookstore.service_library"
	})
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
