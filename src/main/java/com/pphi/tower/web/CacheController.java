package com.pphi.tower.web;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    /**
     * The cache lives in the JVM's heap (Spring's `ConcurrentMapCacheManager` by default). The reason `CacheController`
     * has no injected dependencies is that `@CacheEvict` is handled entirely by Spring AOP. When the HTTP request hits
     * the controller method, Spring's proxy intercepts the call, evicts the cache entries, and then executes the method
     * body — the controller never needs to touch a `CacheManager` directly.
     * <br/>
     * The same mechanism is why `@Cacheable` on `GoogleSheetsRepository.readRanges` works: Spring wraps the repository
     * bean in a proxy that checks the cache before delegating to the real method, and stores the result after.
     */
    @CacheEvict(value = "sheets", allEntries = true)
    @DeleteMapping("/sheets")
    public ResponseEntity<Void> clearSheetsCache() {
        return ResponseEntity.noContent().build();
    }
}
