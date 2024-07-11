package com.sarkesa.palindrome;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.sarkesa.palindrome.model.PalindromeCheck;
import com.sarkesa.palindrome.model.PalindromeCheckRequest;
import com.sarkesa.palindrome.persistence.PalindromeRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.constraints.NotNull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.sarkesa.palindrome.cache.CacheConfig.PALINDROME_RESULTS_CACHE;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PalindromeCheckerIntegrationTest {
    @Autowired private CacheManager cacheManager;
    @Autowired private PalindromeRepository palindromeRepository;

    @Autowired
    private MockMvc mockMvc;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String BASE_PATH = "/api/v1/palindrome";
    private static final String CHECK_PATH = BASE_PATH + "/check";
    private static final String CSV_FILE_PATH = "/tmp/palindrome.csv";

    @AfterEach
    void tearDown() throws Exception {
        deleteFile(CSV_FILE_PATH);
    }

    private @NotNull Cache<Object, Object> getNativeCache() {
        assertThat(cacheManager.getCache(PALINDROME_RESULTS_CACHE)).isNotNull();
        return (Cache<Object, Object>) cacheManager.getCache(PALINDROME_RESULTS_CACHE).getNativeCache();
    }

    @Test
    void checkPalindrome_Post_validRequest() throws Exception {
        Cache<Object, Object> nativeCache = getNativeCache();
        final long initialTestHitCount = nativeCache.stats().hitCount();
        final long initialTestMissCount = nativeCache.stats().missCount();

        assertThat(palindromeRepository.findAll().size()).isZero();

        // First request
        sendRequest("stephen", "kayak");

        // first request, cache miss
        assertThat(nativeCache.stats().hitCount()).isEqualTo(initialTestHitCount);
        assertThat(nativeCache.stats().missCount()).isEqualTo(initialTestMissCount + 1);

        // Wait for async save.
        sleep(1000);

        assertThat(palindromeRepository.findAll())
                .containsExactly(PalindromeCheck.builder().text("kayak").isPalindrome(true).build());

        // 2nd request
        sendRequest("stephen", "kayak");

        // 2nd request, cache hit
        assertThat(nativeCache.stats().hitCount()).isEqualTo(initialTestHitCount + 1);
        assertThat(nativeCache.stats().missCount()).isEqualTo(initialTestMissCount + 1);
        assertThat(palindromeRepository.findAll())
                .containsExactly(PalindromeCheck.builder().text("kayak").isPalindrome(true).build());

        // 3rd request
        sendRequest("stephen", "kayak");

        // 3rd request, cache hit
        assertThat(nativeCache.stats().hitCount()).isEqualTo(initialTestHitCount + 2);
        assertThat(nativeCache.stats().missCount()).isEqualTo(initialTestMissCount + 1);
        assertThat(palindromeRepository.findAll())
                .containsExactly(PalindromeCheck.builder().text("kayak").isPalindrome(true).build());
    }

    private void sendRequest(final String username,
                             final String text) throws Exception {
        PalindromeCheckRequest request = PalindromeCheckRequest.builder()
                .username(username)
                .text(text)
                .build();

        MvcResult mvcResult = this.mockMvc
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
    }

    @SneakyThrows
    private void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
        System.out.println("File deleted successfully!");
    }
}