package com.example.demo.securities;

import com.example.demo.constants.DemoError;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.models.UserAccountModel;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class JwtAuthenticator {

    @Resource
    AuthenticationManager authenticationManager;
    @Resource
    UserService userService;
    @Resource
    PasswordManager passwordManager;
    @Resource
    JwtUserDetailsService jwtUserDetailsService;
    @Resource
    JwtUtil jwtUtil;

    public String createToken(String loginId, String password) throws DemoApiException {
        authenticate(loginId, password);

        UserAccountModel userAccountModel = userService.findAccountByLoginId(loginId);
        if (userAccountModel == null) {
            throw new DemoApiException("User not found: " + loginId, DemoError.USER_NOT_FOUND);
        }

        UserDetails userDetails = jwtUserDetailsService.loadUser(userAccountModel);
        String accessCode = UUID.randomUUID().toString();
        String token = jwtUtil.generateToken(userDetails, accessCode);
        String tokenHash = passwordManager.getSha256Hash(token);

        userAccountModel.setLoginToken(tokenHash);
        userService.save(userAccountModel);

        return token;
    }

    protected void authenticate(String loginId, String password) throws DemoApiException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginId, password));
        } catch (BadCredentialsException e) {
            throw new DemoApiException("Invalid user name or password: " + loginId, e, DemoError.BAD_CREDENTIALS);
        } catch (AuthenticationCredentialsNotFoundException e) {
            throw new DemoApiException("Account was locked due to repetitive invalid login attempts: " + loginId, e, DemoError.USER_AUTHENTICATION_FAILED);
        } catch (AuthenticationException e) {
            throw new DemoApiException("Failed to authenticate user: " + loginId, e, DemoError.USER_AUTHENTICATION_FAILED);
        }
    }

    public void deleteToken() throws DemoApiException {
        UserAccountModel userAccountModel = userService.getCurrentUserAccount();
        if (userAccountModel != null) {
            userAccountModel.setLoginToken(null);
            userService.save(userAccountModel);
        }
    }

}
