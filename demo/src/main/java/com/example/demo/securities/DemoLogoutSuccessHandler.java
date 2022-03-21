package com.example.demo.securities;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DemoLogoutSuccessHandler implements LogoutSuccessHandler {

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // If no DemoApiException has been thrown, set Status 200
        if (!response.isCommitted()) {
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().flush();
        }
    }
}
