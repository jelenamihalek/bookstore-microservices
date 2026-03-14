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
                .path("/book-service/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://book-service"))
				
				
				.build();
	}
}