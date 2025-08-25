package shopmate.apigateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        logger.info("Incoming request: {}", request.getURI().getPath());

        if (request.getURI().getPath().contains("/api/users/auth/login") ||
                request.getURI().getPath().contains("/api/users/auth/register") ||
                request.getURI().getPath().contains("/api/users/auth/validate") ||
                request.getURI().getPath().contains("/api/products/images/") ||
                request.getURI().getPath().contains("/api/products/categories") ||
                request.getURI().getPath().contains("/actuator/prometheus") ||
                request.getURI().getPath().contains("/api/products")) {
            logger.info("Skipping authentication for: {}", request.getURI().getPath());
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header: {}", request.getURI().getPath());
            return this.onError(exchange, "Missing or invalid token", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Invalid JWT token for request: {}", request.getURI().getPath());
            return this.onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }

        logger.info("Authenticated request: {}", request.getURI().getPath());
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}
