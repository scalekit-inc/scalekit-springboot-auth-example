package com.example.scalekit.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        if (oidcUser != null) {
            // Real OIDC user - use actual data
            model.addAttribute("user", oidcUser);
            model.addAttribute("name", oidcUser.getFullName());
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("subject", oidcUser.getSubject());
            model.addAttribute("claims", oidcUser.getClaims());
            model.addAttribute("authorities", oidcUser.getAuthorities());
            model.addAttribute("demoMode", false);
        } else {
            // Demo mode - use mock data
            Map<String, Object> mockClaims = createMockClaims();
            model.addAttribute("name", "Demo User");
            model.addAttribute("email", "demo@example.com");
            model.addAttribute("subject", "demo-user-123");
            model.addAttribute("claims", mockClaims);
            model.addAttribute("authorities", "ROLE_USER");
            model.addAttribute("demoMode", true);
        }
        return "dashboard";
    }


    private Map<String, Object> createMockClaims() {
        Map<String, Object> claims = new HashMap<>();
        long now = Instant.now().getEpochSecond();

        claims.put("sub", "demo-user-123");
        claims.put("name", "Demo User");
        claims.put("given_name", "Demo");
        claims.put("family_name", "User");
        claims.put("email", "demo@example.com");
        claims.put("email_verified", true);
        claims.put("iss", "https://demo.scalekit.io");
        claims.put("aud", "demo-client-id");
        claims.put("iat", now - 3600);
        claims.put("exp", now + 3600);
        claims.put("auth_time", now - 3600);
        claims.put("organization", "Scalekit Demo Organization");
        claims.put("department", "Engineering");
        claims.put("role", "Developer");

        return claims;
    }

}