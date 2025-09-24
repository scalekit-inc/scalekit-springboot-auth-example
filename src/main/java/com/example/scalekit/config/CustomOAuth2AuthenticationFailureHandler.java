package com.example.scalekit.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {

        logger.error("OAuth2 Authentication failed", exception);

        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            logger.error("OAuth2 Error Code: {}", oauth2Exception.getError().getErrorCode());
            logger.error("OAuth2 Error Description: {}", oauth2Exception.getError().getDescription());
            logger.error("OAuth2 Error URI: {}", oauth2Exception.getError().getUri());

            String errorParam = URLEncoder.encode(oauth2Exception.getError().getErrorCode(), StandardCharsets.UTF_8);
            String errorDescription = oauth2Exception.getError().getDescription() != null ?
                URLEncoder.encode(oauth2Exception.getError().getDescription(), StandardCharsets.UTF_8) : "";

            getRedirectStrategy().sendRedirect(request, response,
                "/login?error=" + errorParam + "&description=" + errorDescription);
        } else {
            logger.error("General Authentication Exception: {}", exception.getMessage());
            getRedirectStrategy().sendRedirect(request, response,
                "/login?error=unknown&description=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8));
        }
    }
}