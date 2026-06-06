package com.revcart.product_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ProductCategoryValidatorImpl implements ConstraintValidator<ProductCategoryValidator, String> {

    private String[] allowedValues;

    @Override
    public void initialize(ProductCategoryValidator constraintAnnotation) {
        // Extract all string names from the provided Enum class
        this.allowedValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        // Check if incoming string matches any allowed enum name (case-sensitive)
        return Arrays.asList(allowedValues).contains(s);
    }
}
