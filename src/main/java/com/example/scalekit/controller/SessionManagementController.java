package com.example.scalekit.controller;

import com.example.scalekit.service.SessionManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/sessions")
public class SessionManagementController {

    @Autowired
    private SessionManagementService sessionManagementService;

    @GetMapping
    public String listSessions(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        try {
            // Get current session information
            Map<String, Object> sessionInfo = sessionManagementService.getCurrentSessionInfo();
            Map<String, Object> expiryInfo = sessionManagementService.getTokenExpiryInfo();
            
            model.addAttribute("sessionInfo", sessionInfo);
            model.addAttribute("expiryInfo", expiryInfo);
            model.addAttribute("currentUser", oidcUser);
            model.addAttribute("isTokenExpired", sessionManagementService.isTokenExpired());
            model.addAttribute("isTokenExpiringSoon", sessionManagementService.isTokenExpiringSoon());
            
            return "sessions";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load session information: " + e.getMessage());
            return "sessions";
        }
    }

    @PostMapping("/validate-token")
    @ResponseBody
    public Map<String, Object> validateToken() {
        return sessionManagementService.validateCurrentAccessToken();
    }

    @PostMapping("/refresh-token")
    @ResponseBody
    public Map<String, Object> refreshToken() {
        return sessionManagementService.refreshAccessToken();
    }

}