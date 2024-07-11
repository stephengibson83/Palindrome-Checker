package com.sarkesa.palindrome.model;

import com.sarkesa.palindrome.validation.PalindromeInputConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class PalindromeCheckRequest {
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "username must be alphanumberic only")
    @Schema(required = true, description = "The username for the user performing the request")
    private String username;

    @NotEmpty
    @Size(min = 1, max = 50)
    @PalindromeInputConstraint // This could be achieved with regex but I have included a custom validator to show it can be updated easily
    @Schema(required = true,
            description = "The candidate string for palindrome evaluation. It should be a contiguous sequence of characters without spaces or numbers.")
    private String text;
}
