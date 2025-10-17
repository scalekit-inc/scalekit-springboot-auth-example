package com.example.scalekit.service;

import com.scalekit.ScalekitClient;
import com.scalekit.api.AuthClient;
import com.scalekit.exceptions.APIException;
import com.scalekit.internal.http.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class SessionManagementService {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ScalekitClient scalekitClient;

    /**
     * Get current session information 
     */
    public Map<String, Object> getCurrentSessionInfo() {
        Map<String, Object> sessionInfo = new HashMap<>();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Get basic user info from OIDC
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            sessionInfo.put("userId", oidcUser.getSubject());
            sessionInfo.put("email", oidcUser.getEmail());
            sessionInfo.put("name", oidcUser.getFullName());
        }
        
        // Get token info from OAuth2AuthorizedClient
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            try {
                OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
                
                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                    OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
                    
                    sessionInfo.put("accessToken", accessToken.getTokenValue());
                    sessionInfo.put("tokenType", accessToken.getTokenType().getValue());
                    sessionInfo.put("scopes", accessToken.getScopes());
                    sessionInfo.put("expiresAt", accessToken.getExpiresAt());
                    sessionInfo.put("hasRefreshToken", refreshToken != null);
                    
                    // Add refresh token if available
                    if (refreshToken != null) {
                        sessionInfo.put("refreshToken", refreshToken.getTokenValue());
                        sessionInfo.put("refreshTokenType", "Bearer");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting OAuth2 tokens: " + e.getMessage());
            }
        }
        
        return sessionInfo;
    }

    /**
     * Check if the current access token is expired 
     */
    public boolean isTokenExpired() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
                
                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    Instant now = Instant.now();
                    Instant expiresAt = authorizedClient.getAccessToken().getExpiresAt();
                    return expiresAt != null && now.isAfter(expiresAt);
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking token expiry: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Check if the current access token is expiring soon
     */
    public boolean isTokenExpiringSoon() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
                
                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    Instant now = Instant.now();
                    Instant expiresAt = authorizedClient.getAccessToken().getExpiresAt();
                    if (expiresAt != null) {
                        long minutesUntilExpiry = ChronoUnit.MINUTES.between(now, expiresAt);
                        return minutesUntilExpiry <= 5;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking token expiry: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Get the current access token value
     */
    public String getCurrentAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
            
            if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                return authorizedClient.getAccessToken().getTokenValue();
            }
        }
        
        return null;
    }

    /**
     * Get token expiry information for display 
     */
    public Map<String, Object> getTokenExpiryInfo() {
        Map<String, Object> expiryInfo = new HashMap<>();
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
                
                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    Instant now = Instant.now();
                    Instant expiresAt = authorizedClient.getAccessToken().getExpiresAt();
                    
                    if (expiresAt != null) {
                        long minutesUntilExpiry = ChronoUnit.MINUTES.between(now, expiresAt);
                        
                        expiryInfo.put("expiresAt", expiresAt);
                        expiryInfo.put("minutesUntilExpiry", minutesUntilExpiry);
                        expiryInfo.put("isExpired", now.isAfter(expiresAt));
                        expiryInfo.put("isExpiringSoon", minutesUntilExpiry <= 5);
                        
                        // Simple expiry display
                        if (minutesUntilExpiry > 60) {
                            long hours = minutesUntilExpiry / 60;
                            expiryInfo.put("expiryDisplay", String.format("%d hours", hours));
                        } else {
                            expiryInfo.put("expiryDisplay", String.format("%d minutes", minutesUntilExpiry));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting token expiry info: " + e.getMessage());
        }
        
        return expiryInfo;
    }

    /**
     * Validate the current access token using Scalekit SDK
     */
    public Map<String, Object> validateCurrentAccessToken() {
        Map<String, Object> validationResult = new HashMap<>();
        
        try {
            String accessToken = getCurrentAccessToken();
            
            if (accessToken == null || accessToken.isEmpty()) {
                validationResult.put("valid", false);
                validationResult.put("error", "No access token found");
                return validationResult;
            }
            
            AuthClient authClient = scalekitClient.authentication();
            
            // Validate the token and get claims
            Map<String, Object> claims = authClient.validateAccessTokenAndGetClaims(accessToken);
            
            validationResult.put("valid", true);
            validationResult.put("claims", claims);
            validationResult.put("message", "Token is valid");
            
            // Extract useful information from claims
            if (claims != null) {
                validationResult.put("userId", claims.get("sub"));
                validationResult.put("email", claims.get("email"));
                validationResult.put("name", claims.get("name"));
                validationResult.put("exp", claims.get("exp"));
                validationResult.put("iat", claims.get("iat"));
                validationResult.put("iss", claims.get("iss"));
                validationResult.put("aud", claims.get("aud"));
            }
            
        } catch (APIException e) {
            validationResult.put("valid", false);
            validationResult.put("error", "Token validation failed: " + e.getMessage());
            validationResult.put("apiError", true);
        } catch (Exception e) {
            validationResult.put("valid", false);
            validationResult.put("error", "Unexpected error during token validation: " + e.getMessage());
            validationResult.put("apiError", false);
        }
        
        return validationResult;
    }

    /**
     * Simple boolean check for token validation
     */
    public boolean isTokenValid() {
        try {
            String accessToken = getCurrentAccessToken();
            if (accessToken == null || accessToken.isEmpty()) {
                return false;
            }
            
            AuthClient authClient = scalekitClient.authentication();
            return authClient.validateAccessToken(accessToken);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh the access token using the refresh token
     */
    public Map<String, Object> refreshAccessToken() {
        Map<String, Object> refreshResult = new HashMap<>();
        
        try {
            // Get current refresh token
            String refreshToken = getCurrentRefreshToken();
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                refreshResult.put("success", false);
                refreshResult.put("error", "No refresh token available");
                return refreshResult;
            }
            
            AuthClient authClient = scalekitClient.authentication();
            
            // Call Scalekit SDK to refresh the token
            AuthenticationResponse authResponse = authClient.refreshAccessToken(refreshToken);
            
            if (authResponse != null) {
                // Update the OAuth2AuthorizedClient with new tokens
                boolean tokensUpdated = updateOAuth2AuthorizedClientWithNewTokens(authResponse);
                
                refreshResult.put("success", true);
                refreshResult.put("message", "Tokens refreshed successfully");
                refreshResult.put("newAccessToken", authResponse.getAccessToken());
                refreshResult.put("newRefreshToken", authResponse.getRefreshToken());
                refreshResult.put("newIdToken", authResponse.getIdToken());
                refreshResult.put("tokensUpdatedInContext", tokensUpdated);
                
                if (tokensUpdated) {
                    refreshResult.put("note", "New tokens have been updated in the session context.");
                } else {
                    refreshResult.put("note", "New tokens obtained but could not be updated in session context.");
                }
            } else {
                refreshResult.put("success", false);
                refreshResult.put("error", "No response from token refresh");
            }
            
        } catch (APIException e) {
            refreshResult.put("success", false);
            refreshResult.put("error", "Token refresh failed: " + e.getMessage());
            refreshResult.put("apiError", true);
        } catch (Exception e) {
            refreshResult.put("success", false);
            refreshResult.put("error", "Unexpected error during token refresh: " + e.getMessage());
            refreshResult.put("apiError", false);
        }
        
        return refreshResult;
    }

    /**
     * Get the current refresh token
     */
    public String getCurrentRefreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
            
            if (authorizedClient != null && authorizedClient.getRefreshToken() != null) {
                return authorizedClient.getRefreshToken().getTokenValue();
            }
        }
        
        return null;
    }

    /**
     * Update the OAuth2AuthorizedClient with new tokens from the refresh response
     */
    private boolean updateOAuth2AuthorizedClientWithNewTokens(AuthenticationResponse authResponse) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                // Get the current authorized client
                OAuth2AuthorizedClient currentClient = authorizedClientService
                    .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());
                
                if (currentClient != null) {
                    // Get the client registration
                    ClientRegistration clientRegistration = clientRegistrationRepository
                        .findByRegistrationId(oauth2Token.getAuthorizedClientRegistrationId());
                    
                    if (clientRegistration != null) {
                        // Create new access token
                        OAuth2AccessToken newAccessToken = new OAuth2AccessToken(
                            OAuth2AccessToken.TokenType.BEARER,
                            authResponse.getAccessToken(),
                            Instant.now(),
                            Instant.now().plusSeconds(3600) // Default 1 hour expiry
                        );
                        
                        // Create new refresh token if provided
                        OAuth2RefreshToken newRefreshToken = null;
                        if (authResponse.getRefreshToken() != null) {
                            newRefreshToken = new OAuth2RefreshToken(authResponse.getRefreshToken(), Instant.now());
                        }
                        
                        // Create new authorized client with updated tokens
                        OAuth2AuthorizedClient updatedClient = new OAuth2AuthorizedClient(
                            clientRegistration,
                            oauth2Token.getName(),
                            newAccessToken,
                            newRefreshToken
                        );
                        
                        // Save the updated client
                        authorizedClientService.saveAuthorizedClient(updatedClient, oauth2Token);
                        
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating OAuth2AuthorizedClient with new tokens: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

}
