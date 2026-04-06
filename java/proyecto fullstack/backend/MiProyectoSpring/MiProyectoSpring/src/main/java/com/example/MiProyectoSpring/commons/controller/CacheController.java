package com.ptesa.bmc.coreservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints for clearing caches.
 */
@RestController
@RequestMapping("/caches")
@AllArgsConstructor
public class CacheController {

    // Fields

    private final CacheManager cacheManager;


    // Logic

    /**
     * Clears all entries for the given cache
     * @param cacheName Cache name
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<String> evictCache(@PathVariable String cacheName) {
        Cache cache = this.cacheManager.getCache(cacheName);

        if (cache == null) {
            return new ResponseEntity<>("Cache does not exist", HttpStatus.NOT_FOUND);
        }

        cache.clear();
        return new ResponseEntity<>("Cache cleared successfully", HttpStatus.OK);
    }
}
