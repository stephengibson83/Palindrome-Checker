package com.sarkesa.palindrome.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String id;
    private String timestamp;
    private String message;
    private Integer status;
    private String error;
    private String path;
}
