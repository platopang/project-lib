package com.example.demo.controllers.filters;

import com.example.demo.models.UserAccountModel;
import com.example.demo.securities.PasswordManager;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter  extends OncePerRequestFilter {

    @Resource
    protected UserService userService;
    @Resource
    protected JwtUserDetailsService jwtUserDetailsService;
    @Resource
    protected JwtUtil jwtUtil;
    @Resource
    protected PasswordManager passwordManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String loginId = null;
        String jwt = null;
        String jwtHash = null;

        // Retrieve JWT from RequestHeader
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwt = requestTokenHeader.substring("Bearer ".length());
            try {
                loginId = jwtUtil.getUsernameFromToken(jwt);
                jwtHash = passwordManager.getSha256Hash(jwt);
            } catch (IllegalArgumentException e) {
                logger.debug("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.debug("JWT Token has expired");
            }
        }

        // Skip validation if JWT LoginID same as in Security Context
        boolean needValidate = false;
        if (loginId != null) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                needValidate = true;
            } else {
                UserAccountModel userAccountModel = (UserAccountModel) SecurityContextHolder.getContext().getAuthentication().getDetails();
                if (!loginId.equals(userAccountModel.getLoginId())) {
                    needValidate = true;
                }
            }
        }

        // Validate JWT
        if (needValidate) {
            UserAccountModel userAccountModel = userService.findAccountByLoginId(loginId);

            if (userAccountModel == null) {
                throw new UsernameNotFoundException("User not found: " + loginId);
            }

            if (userAccountModel.getLoginToken() != null && !userAccountModel.getLoginToken().equals(jwtHash)) {
                throw new UsernameNotFoundException("User not found: " + loginId);
            }

            UserDetails userDetails = this.jwtUserDetailsService.loadUser(userAccountModel);

            // if token is valid, manually set Spring Security authentication
            if (jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(userAccountModel);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        chain.doFilter(request, response);
    }
}
