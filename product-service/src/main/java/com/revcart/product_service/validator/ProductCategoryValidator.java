package com.revcart.product_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductCategoryValidatorImpl.class) // Links annotation to logic
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER}) // Where it applies
@Retention(RetentionPolicy.RUNTIME) // Available at runtime for validation execution
public @interface ProductCategoryValidator {

    // Required property: The target Enum class to validate against
    Class<? extends Enum<?>> enumClass();

    // Required property: Default error message if validation fails
    String message() default "Invalid value provided for this field.";

    // Required property: Allows splitting validation into specific steps
    Class<?>[] groups() default {};

    // Required property: Carries metadata payloads used by validation clients
    Class<? extends Payload>[] payload() default {};
}
