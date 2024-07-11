package com.sarkesa.palindrome.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PalindromeInputValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PalindromeInputConstraint {

    String message() default "Value cannot contain a space or numeric character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}