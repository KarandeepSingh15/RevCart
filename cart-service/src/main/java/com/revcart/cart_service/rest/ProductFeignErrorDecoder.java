package com.revcart.cart_service.rest;

import com.revcart.cart_service.exception.ProductNotAvailableException;
import com.revcart.cart_service.exception.ValidationException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        return switch (response.status()) {

            case 404 -> new ProductNotAvailableException("Product Not found");
            case 400 -> new ValidationException("Bad request to product service");

            default -> new ErrorDecoder.Default().decode(s, response);
        };
    }
}
