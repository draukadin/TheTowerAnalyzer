package com.pphi.tower.web;

import com.pphi.tower.config.OAuthStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuthStateService oAuthStateService;

    public AuthController(OAuthStateService oAuthStateService) {
        this.oAuthStateService = oAuthStateService;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", oAuthStateService.getStatus().name().toLowerCase());
        if (oAuthStateService.getStatus() == OAuthStateService.Status.PENDING) {
            result.put("url", oAuthStateService.getAuthUrl());
        }
        return result;
    }
}
