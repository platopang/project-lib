package com.example.demo.securities;

import com.example.demo.exceptions.DemoApiException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DemoLogoutHandler implements LogoutHandler {

    @Resource
    private JwtAuthenticator jwtAuthenticator;
    @Resource
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String accessToken = requestTokenHeader.substring("Bearer ".length());
            try {
                jwtAuthenticator.deleteTokenByAccessToken(accessToken);
            } catch (DemoApiException e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
            }
        }
    }

}