package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.infrastructure.security.JwtAuthenticationFilter;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> {})
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/register").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/salons").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/salons/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/salons/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/services/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/services/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/services/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/service-categories/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/service-categories/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/service-categories/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/clients/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/clinical-records/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
