package com.bookstore.api_gateaway.autentification;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;


import com.bookstore.service_library.dtos.UserDTO;

import org.springframework.security.config.Customizer;


@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAutentification {

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
        
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange

            		//USERS
            		    .pathMatchers(HttpMethod.POST,"/users/register").permitAll()
            		    .pathMatchers(HttpMethod.GET,"/users/email").permitAll()

            		  
            		    .pathMatchers(HttpMethod.POST,"/users/admin").hasRole("ADMIN")

            		    .pathMatchers(HttpMethod.GET,"/users/**").permitAll()
            		    .pathMatchers(HttpMethod.PUT, "/users/admin/**").hasRole("ADMIN")
            		    .pathMatchers(HttpMethod.PUT, "/users/**").permitAll()

            		    .pathMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

            		   // .pathMatchers(HttpMethod.PUT,"/users/**").permitAll()


                // BOOKS
                .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/books").hasAnyRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")

                // ORDERS
                .pathMatchers(HttpMethod.POST, "/orders").hasRole("USER")
                .pathMatchers(HttpMethod.GET, "/orders/my").hasRole("USER")

                .pathMatchers(HttpMethod.GET, "/orders/all").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/orders/admin/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/orders/*").hasRole("USER")

                // sve ostalo
                .anyExchange().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

     @Bean
    ReactiveUserDetailsService userDetailsService(WebClient.Builder webClientBuilder) {

       // WebClient client = webClientBuilder.baseUrl("http://user-service:8100").build();
    	 
    		 WebClient client = webClientBuilder.baseUrl("http://localhost:8100").build();
    		// WebClient client = webClientBuilder.build();
        return username -> client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/email")
                        .queryParam("email", username)
                        .build()
                )
                .retrieve()
               .bodyToMono(UserDTO.class)
                .map(dto -> {
                    System.out.println("ROLE IZ BAZE: " + dto.getRole()); // debug

                    return User.withUsername(dto.getEmail())
                            .password(dto.getPassword())
                            .authorities("ROLE_" + dto.getRole().toUpperCase())
                            .build();
                });
        

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
