package com.danydandy.SalonSpa.config;

import com.danydandy.SalonSpa.domain.ports.in.SubscriptionUseCase;
import com.danydandy.SalonSpa.infrastructure.security.JwtAuthenticationFilter;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import com.danydandy.SalonSpa.infrastructure.security.SubscriptionAccessFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            SubscriptionUseCase subscriptionUseCase
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/bootstrap").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/register").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/salons").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/salons/*/logo").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/salons/*/logo").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/salons/**").hasRole("SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/salons/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/branches/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/users").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN_USER", "SUPER_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/services/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/services/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/service-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/service-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/service-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/product-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/product-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/product-categories/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/clients/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/clients/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/clients/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/clinical-records/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/appointments/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/appointments/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.POST, "/api/sales/**").hasRole("ADMIN_USER")
                        .pathMatchers(HttpMethod.PUT, "/api/sales/**").hasRole("ADMIN_USER")
                        .pathMatchers("/api/subscriptions/**").hasRole("SUPER_ADMIN")
                        // The platform owner (SUPER_ADMIN) manages salons, subscriptions, branches and users, but does not work
                        // inside a salon: the salon's clients, clinical records (health data), appointments, sales and
                        // commissions are for the salon's own people. The dashboard (/api/dashboard) stays open to it.
                        .pathMatchers("/api/clients/**", "/api/clinical-records/**", "/api/appointments/**",
                                "/api/sales/**", "/api/commissions/**").hasAnyRole("ADMIN_USER", "STAFF_USER")
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // After authorization: unauthenticated/forbidden requests are answered first; this one blocks salons
                // with no active subscription and routes of features their plan does not include.
                .addFilterAfter(new SubscriptionAccessFilter(subscriptionUseCase), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }
}
