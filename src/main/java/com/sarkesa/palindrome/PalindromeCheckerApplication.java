package com.sarkesa.palindrome;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@OpenAPIDefinition(info = @Info(
        title = "CME API Challenge",
        version = "1.0",
        description = "Stephen Gibson's solution for the CME API challenge "
                + "<br><br>The POST operation can be used to check if input 'text' is a palindrome."
                + "<br><br>The GET operation can be used to retrieve all cached results."
                + "<br><br><h4>Assumptions made in solution:</h4>"
                + "<ul>"
                + "<li>Results are not to be stored per user</li>"
                + "<li>Username will not be supplied in the body in the future if authication was added - it would be extracted for OAuth token</li>"
                + "<li>A single character is a palindrome</li>"
                + "<li>Zero character length input text should be rejected</li>"
                + "<li>Case sensitivity is ignored when determining if input text is a palindrome</li>"
                + "<li>The username should only be alphanumeric</li>"
                + "<li>No accented characters are permitted in the username</li>"
                + "<li>The username should be a maximum of 25 characters</li>"
                + "<li>The text value should be a maximum of 50 characters</li>"
                + "<li>Accented characters (e.g. ć, é, ö) are permitted in the input text</li>"
                + "<li>Accented characters will be treated as NOT equalling their none accented version. i.e. á is not equal to a.</li>"
                + "<li>Special characters (e.g. £, $, &) are permitted in the input text</li>"
                + "</ul>"))
public class PalindromeCheckerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalindromeCheckerApplication.class, args);
        log.info("*** PALINDROME CHECKER APP HAS STARTED ***");
    }
}