package com.revcart.order_service.rest;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class HeaderRelayInterceptor implements RequestInterceptor {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-Role";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String roleHeader = request.getHeader(USER_ROLE_HEADER);
            if (roleHeader != null) {
                template.header("X-Role", roleHeader);
            }

            String userIdHeader = request.getHeader(USER_ID_HEADER);
            if (userIdHeader != null) {
                template.header("X-User-Id", userIdHeader);
            }
        }
    }
}

