package com.sarkesa.palindrome.api;

import com.sarkesa.palindrome.model.PalindromeCheck;
import com.sarkesa.palindrome.model.PalindromeCheckRequest;
import com.sarkesa.palindrome.service.PalindromeCheckerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/palindrome", produces = MediaType.APPLICATION_JSON_VALUE)
public class PalindromeCheckerController {

    private PalindromeCheckerService palindromeCheckerService;

    public PalindromeCheckerController(final PalindromeCheckerService palindromeCheckerService) {
        this.palindromeCheckerService = palindromeCheckerService;
    }

    @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Check if input text is a palindrome")
    public PalindromeCheck checkPalindrome(@Valid @RequestBody final PalindromeCheckRequest palindromeCheckRequest) {
        // Generating a unique request ID that will be returned in the response, but also added to logs for traceability
        final UUID requestId = UUID.randomUUID();
        MDC.put("requestId", requestId.toString());
        MDC.put("user", palindromeCheckRequest.getUsername());
        log.trace("Check palindrome request {}", palindromeCheckRequest);

        final boolean result = palindromeCheckerService.isPalindrome(palindromeCheckRequest.getText());
        log.info("For input text [{}] the palindrome result: [{}]", palindromeCheckRequest.getText(), result);
        removeFieldsFromMdc();

        return PalindromeCheck.builder().text(palindromeCheckRequest.getText()).isPalindrome(result).id(requestId).build();
    }

    @GetMapping("cache-contents")
    @Operation(summary = "Retrieve all cached results for input text.")
    public Map<Object, Object> getCacheContents() {
        return palindromeCheckerService.getCachedPalindromeResults();
    }

    private void removeFieldsFromMdc() {
        MDC.clear();
    }
}
