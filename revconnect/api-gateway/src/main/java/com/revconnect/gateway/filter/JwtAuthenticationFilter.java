package com.revconnect.gateway.filter;

import com.revconnect.gateway.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// JWT filter applied to all protected routes in the API Gateway.
// Validates the Bearer token, extracts userId, and passes it as X-User-Id header to downstream services.
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public String name() {
        return "JwtAuthentication";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Public endpoints — no token required
            if (path.contains("/api/auth/login") || path.contains("/api/users/register")) {
                return chain.filter(exchange);
            }

            // Like/comment count endpoints are public
            if (path.matches(".*/likes/post/\\d+/count") || path.matches(".*/comments/post/\\d+/count")) {
                return chain.filter(exchange);
            }

            // All other requests require a valid Bearer token
            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            try {
                // Extract userId from token and forward it as a header
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    public static class Config {
        // No extra config needed
    }
}
