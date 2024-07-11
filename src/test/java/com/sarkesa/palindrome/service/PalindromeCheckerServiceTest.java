package com.sarkesa.palindrome.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.sarkesa.palindrome.model.PalindromeCheck;
import com.sarkesa.palindrome.persistence.PalindromeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.sarkesa.palindrome.cache.CacheConfig.PALINDROME_RESULTS_CACHE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class PalindromeCheckerServiceTest {
    @Mock
    private CaffeineCacheManager cacheManager;

    @Mock
    private PalindromeRepository palindromeRepository;

    @Mock
    private Cache<Object, Object> cache;

    private PalindromeCheckerService palindromeCheckerService;

    @BeforeEach
    void setUp() throws IOException {
        initMocks(this);

        palindromeCheckerService = new PalindromeCheckerService(cacheManager, palindromeRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a",
            "aa",
            "aaa",
            "level",
            "rotor",
            "kayak",
            "racecar",
            "raceCar",
            "noon",
            "madam",
            "Madam",
            "refer",
            "tenet",
            "civic",
            "poop",
            "mum",
            "dad",
            "pop",
            "deed",
            "redder",
            "otto",
            "Ava",
            "aya",
            "eye",
            "peep",
            "rotator",
            "murdrum",
            "&^&^&",
            "!@£$%^&*()(*&^%$£@!"})
    void isPalindrome_shouldCorrectlyProcessPalindromeInputs(final String input) {
        verify(cacheManager, times(1)).getCache(PALINDROME_RESULTS_CACHE); // This gets call in constructor

        assertTrue(palindromeCheckerService.isPalindrome(input));

        verify(palindromeRepository, times(1)).save(PalindromeCheck.builder().text(input).isPalindrome(true).build());
        verifyNoMoreInteractions(palindromeRepository, cacheManager, cache);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "levels",
            "motor",
            "kayark",
            "Pacecar",
            "moon",
            "man",
            "prefer",
            "tennis",
            "civil",
            "pool",
            "mummy",
            "daddy",
            "!@£$%^&**()(*&^%$£@!"})
    void isPalindrome_shouldCorrectlyProcessNonPalindromeInputs(final String input) {
        verify(cacheManager, times(1)).getCache(PALINDROME_RESULTS_CACHE); // This gets call in constructor

        assertFalse(palindromeCheckerService.isPalindrome(input));

        verify(palindromeRepository, times(1)).save(PalindromeCheck.builder().text(input).isPalindrome(false).build());
        verifyNoMoreInteractions(palindromeRepository, cacheManager, cache);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void isPalindrome_shouldCorrectlyProcessNullAndEmptyInputs(final String input) {
        verify(cacheManager, times(1)).getCache(PALINDROME_RESULTS_CACHE); // This gets call in constructor

        assertFalse(palindromeCheckerService.isPalindrome(input));

        verifyNoMoreInteractions(palindromeRepository, cacheManager, cache);
    }

    @Test
    void getCachedPalindromeResults_shouldHandleCacheNotCreatedYet() {
        when(cacheManager.getCache(anyString())).thenReturn(null);

        Map<Object, Object> result = palindromeCheckerService.getCachedPalindromeResults();

        assertTrue(result.isEmpty());
        verify(cacheManager, times(2)).getCache(PALINDROME_RESULTS_CACHE);
        verifyNoMoreInteractions(palindromeRepository, cacheManager, cache);
    }

    @Test
    void getCachedPalindromeResults_shouldGetCachedResults() {
        CaffeineCache caffeineCache = new CaffeineCache(PALINDROME_RESULTS_CACHE, cache);
        ConcurrentMap<Object, Object> cachedMap = new ConcurrentHashMap();
        cachedMap.put("test", false);

        when(cacheManager.getCache(anyString())).thenReturn(caffeineCache);
        when(cache.asMap()).thenReturn(cachedMap);

        Map<Object, Object> result = palindromeCheckerService.getCachedPalindromeResults();

        assertEquals(result.size(), 1);
        assertEquals(result.get("test"), false);

        verify(cacheManager, times(2)).getCache(PALINDROME_RESULTS_CACHE);
        verify(cache, times(1)).asMap();
        verifyNoMoreInteractions(palindromeRepository, cacheManager, cache);
    }
}