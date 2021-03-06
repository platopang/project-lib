package com.example.demo.controllers.filters;

import com.example.demo.exceptions.DemoApiException;
import com.example.demo.securities.JwtAuthenticator;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Resource
    private JwtAuthenticator jwtAuthenticator;
    @Resource
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            String accessToken = requestTokenHeader.substring("Bearer ".length());
            try {
                jwtAuthenticator.authenticateByAccessToken(accessToken);
            } catch (DemoApiException e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
