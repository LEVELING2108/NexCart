package com.ecommerce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() instanceof JwtAuthenticationToken)
                .map(c -> (JwtAuthenticationToken) c.getAuthentication())
                .map(auth -> (Jwt) auth.getPrincipal())
                .flatMap(jwt -> {
                    String userId = jwt.getSubject(); // Keycloak uses 'sub' for user id
                    ServerWebExchange modifiedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-User-Id", userId)
                                    .build())
                            .build();
                    return chain.filter(modifiedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
