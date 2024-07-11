package com.sarkesa.palindrome.validation;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.nonNull;

public class PalindromeInputValidator implements ConstraintValidator<PalindromeInputConstraint, String> {

    @Override
    public boolean isValid(final String input,
                           final ConstraintValidatorContext cxt) {
        return nonNull(input)
                && !StringUtils.containsWhitespace(input)
                && input.chars().noneMatch(Character::isDigit);
    }
}