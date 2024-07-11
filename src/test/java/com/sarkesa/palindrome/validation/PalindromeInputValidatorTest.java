package com.sarkesa.palindrome.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PalindromeInputValidatorTest {
    private PalindromeInputValidator palindromeInputValidator;

    @BeforeEach
    void setUp() {
        palindromeInputValidator = new PalindromeInputValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello",
            "world",
            "veryveryveryveryveryveryveryveryveryveryveryverylongstring",
            "a",
    })
    void isValid_shouldAcceptValidInputs(final String input) {
        assertTrue(palindromeInputValidator.isValid(input, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "hello world",
            "1/1/2024",
            "test1",
            " test",
            "test ",
    })
    void isValid_shouldRejectInvalidInputs(final String input) {
        assertFalse(palindromeInputValidator.isValid(input, null));
    }
}