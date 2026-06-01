package com.revcart.gateway.filter;

import com.revcart.common_security.config.JwtConstants;
import com.revcart.common_security.service.JwtService;
import com.revcart.gateway.validator.RouteValidator;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        implements GlobalFilter {

    private final RouteValidator routeValidator;
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (!routeValidator.isSecured(exchange.getRequest())) {

            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return markUnauthorized(exchange);
        }
        String token = authHeader.substring(7);

        if (!jwtService.isValid(token)) {
            return markUnauthorized(exchange);
        }
        Claims claims = jwtService.extractClaims(token);
        Long userId = claims.get(JwtConstants.USER_ID, Long.class);
        String role = claims.get(JwtConstants.ROLE, String.class);
        String email = claims.getSubject();
        
        ServerHttpRequest request = exchange.getRequest().mutate().headers(headers -> {
            headers.remove(JwtConstants.HEADER_USER_ID);
            headers.remove(JwtConstants.HEADER_ROLE);
            headers.remove(JwtConstants.HEADER_USER_EMAIL);
            headers.add(JwtConstants.HEADER_USER_ID, userId.toString());
            headers.add(JwtConstants.HEADER_USER_EMAIL, email);
            headers.add(JwtConstants.HEADER_ROLE, role);
        }).build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Nonnull
    private static Mono<Void> markUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse()
                .setComplete();
    }
}
