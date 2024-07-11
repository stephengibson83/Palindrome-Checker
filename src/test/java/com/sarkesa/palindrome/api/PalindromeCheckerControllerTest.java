package com.sarkesa.palindrome.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarkesa.palindrome.model.PalindromeCheck;
import com.sarkesa.palindrome.model.PalindromeCheckRequest;
import com.sarkesa.palindrome.persistence.PalindromeRepository;
import com.sarkesa.palindrome.service.PalindromeCheckerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PalindromeCheckerControllerTest {

    @MockBean
    private PalindromeCheckerService palindromeCheckerService;

    @MockBean
    private PalindromeRepository palindromeRepository;

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String BASE_PATH = "/api/v1/palindrome";
    private static final String CHECK_PATH = BASE_PATH + "/check";
    private static final String CACHE_CONTENTS_PATH = BASE_PATH + "/cache-contents";

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    void checkPalindrome_baseReturns404() throws Exception {
        this.mockMvc
                .perform(get(BASE_PATH))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Get_methodNotAllowed() throws Exception {
        this.mockMvc
                .perform(get(CHECK_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Put_methodNotAllowed() throws Exception {
        this.mockMvc
                .perform(put(CHECK_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Patch_methodNotAllowed() throws Exception {
        this.mockMvc
                .perform(patch(CHECK_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Delete_methodNotAllowed() throws Exception {
        this.mockMvc
                .perform(delete(CHECK_PATH))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Post_handleUnexpectedError() throws Exception {
        when(palindromeCheckerService.isPalindrome(anyString())).thenThrow(RuntimeException.class);

        PalindromeCheckRequest request = PalindromeCheckRequest.builder()
                .username("stephen")
                .text("kayak")
                .build();

        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn();
    }

    @Test
    void checkPalindrome_Post_validRequest() throws Exception {
        when(palindromeCheckerService.isPalindrome(anyString())).thenReturn(true);

        PalindromeCheckRequest request = PalindromeCheckRequest.builder()
                .username("stephen")
                .text("kayak")
                .build();

        final MvcResult mvcResult = this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        final PalindromeCheck result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PalindromeCheck.class);

        assertEquals(result.getText(), request.getText());
        assertTrue(result.getIsPalindrome());
        assertNotNull(result.getId());
        verify(palindromeCheckerService, times(1)).isPalindrome(eq("kayak"));
    }

    @Test
    void checkPalindrome_Post_emptyBody() throws Exception {
        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Post_incorrectMediaType() throws Exception {
        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.TEXT_XML))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }

    @Test
    void checkPalindrome_Post_missingAllFields() throws Exception {
        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "12345678901234567890123456",
            "stephen gibson",
            "stephén",
            "stephen_gibson"
    })
    void checkPalindrome_Post_invalidUserName(final String username) throws Exception {
        PalindromeCheckRequest request = PalindromeCheckRequest.builder()
                .username(username)
                .text("kayak")
                .build();

        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "madam kayak",
            "1kayak1",
            "121"
    })
    void checkPalindrome_Post_invalidInput(final String input) throws Exception {
        PalindromeCheckRequest request = PalindromeCheckRequest.builder()
                .username("Stephen")
                .text(input)
                .build();

        this.mockMvc
                .perform(post(CHECK_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        verifyNoInteractions(palindromeCheckerService);
    }


    @Test
    void getCacheContents_shouldReturnOk() throws Exception {
        when(palindromeCheckerService.getCachedPalindromeResults()).thenReturn(Map.of());

        final MvcResult mvcResult = this.mockMvc
                .perform(get(CACHE_CONTENTS_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        final Map<Object, Object> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        assertTrue(result.isEmpty());
        verify(palindromeCheckerService, times(1)).getCachedPalindromeResults();
    }
}