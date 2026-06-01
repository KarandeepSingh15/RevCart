package com.revcart.gateway.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteValidator {

    private static final List<String> OPEN_API_ENDPOINTS =
            List.of(
                    "/auth/login",
                    "/auth/register/customer",
                    "/auth/register/seller",
                    "/actuator/health"
            );

    public boolean isSecured(ServerHttpRequest request) {

        return OPEN_API_ENDPOINTS.stream()
                .noneMatch(
                        uri ->
                                request.getURI()
                                        .getPath()
                                        .contains(uri)
                );
    }
}
