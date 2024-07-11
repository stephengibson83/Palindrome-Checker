package com.sarkesa.palindrome.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.sarkesa.palindrome.model.PalindromeCheck;
import com.sarkesa.palindrome.persistence.PalindromeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.sarkesa.palindrome.cache.CacheConfig.PALINDROME_RESULTS_CACHE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class PalindromeCheckerService {
    private final CaffeineCacheManager cacheManager;
    private final PalindromeRepository palindromeRepository;

    public PalindromeCheckerService(final CaffeineCacheManager cacheManager,
                                    final PalindromeRepository palindromeRepository) {
        this.cacheManager = cacheManager;
        this.palindromeRepository = palindromeRepository;
        populateCache();
    }

    @Cacheable(PALINDROME_RESULTS_CACHE)
    public boolean isPalindrome(final String input) {
        if (Strings.isEmpty(input)) {
            log.debug("Input is empty - this cannot be a palindrome");
            return false;
        } else if (input.length() == 1) {
            log.debug("Input length is only 1 - this is a palindrome");
            addResultToPersistenceLayer(input, true);
            return true;
        } else {
            final String reversed = new StringBuilder(input).reverse().toString();
            final boolean result = input.equalsIgnoreCase(reversed);
            log.debug("Input {} a palindrome", result ? "IS" : "IS NOT");
            addResultToPersistenceLayer(input, result);
            return result;
        }
    }

    public Map<Object, Object> getCachedPalindromeResults() {
        final CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(PALINDROME_RESULTS_CACHE);

        if (isNull(caffeineCache)) {
            return Map.of();
        }

        final Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        return nativeCache.asMap();
    }

    private void addResultToPersistenceLayer(final String input, final boolean result) {
        palindromeRepository.save(PalindromeCheck.builder().text(input).isPalindrome(result).build());
    }

    private void populateCache() {
        final CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(PALINDROME_RESULTS_CACHE);
        if (nonNull(caffeineCache)) {
            final List<PalindromeCheck> savedChecks = palindromeRepository.findAll();
            savedChecks.forEach(palindromeCheck -> caffeineCache.put(palindromeCheck.getText(), palindromeCheck.getIsPalindrome()));
        }
    }
}
