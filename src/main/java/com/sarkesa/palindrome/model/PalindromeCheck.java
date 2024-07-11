package com.sarkesa.palindrome.model;

import com.opencsv.bean.CsvBindByPosition;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PalindromeCheck {
    @CsvBindByPosition(position = 0, required = true)
    @Schema(description = "The text that was supplied in the request.")
    private String text;

    @CsvBindByPosition(position = 1, required = true)
    @Schema(description = "The main result of whether the text was a palindrome or not.")
    private Boolean isPalindrome;

    @Schema(description = "A unique ID to correlate with logs.")
    private UUID id;
}
