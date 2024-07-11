package com.sarkesa.palindrome.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String PALINDROME_RESULTS_CACHE = "palindromeCache";

    @Bean
    public CaffeineCacheManager caffeineCacheManager(final Caffeine<Object, Object> caffeine) {
        final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(PALINDROME_RESULTS_CACHE);
        caffeineCacheManager.setCaffeine(caffeine);
        return caffeineCacheManager;
    }

    @Bean
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder().maximumSize(1000).recordStats();
    }
}
