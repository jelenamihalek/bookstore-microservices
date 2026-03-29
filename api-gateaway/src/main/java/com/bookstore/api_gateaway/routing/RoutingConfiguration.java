package com.bookstore.api_gateaway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

	@Bean
	RouteLocator gatewayRouting(RouteLocatorBuilder builder) {
		return builder.routes().route("book-service", r -> r
                .path("/books/**")
                .uri("lb://book-service"))
				
				.route("user-service", r -> r
		                .path("/users/**")
		                .uri("lb://user-service"))
				
				.route("order-service", r -> r
					    .path("/orders/**")
		                .uri("lb://order-service"))
				
				.route("payment-service", r -> r
					    .path("/payments/**")
					    .uri("lb://payment-service"))
				.route("notification-service", r -> r
					    .path("/notifications/**")
					    .uri("lb://notification-service"))
				
				.build();
	}
}