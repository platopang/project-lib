package com.example.demo.securities;

import com.example.demo.constants.DemoError;
import com.example.demo.dtos.TokenDto;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.models.UserAccountModel;
import com.example.demo.models.UserOAuth2TokenModel;
import com.example.demo.service.JwtUserDetailsService;
import com.example.demo.service.UserService;
import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component
public class JwtAuthenticator {

    private final Log logger = LogFactory.getLog(JwtAuthenticator.class);

    @Lazy
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserService userService;
    @Resource
    private PasswordManager passwordManager;
    @Resource
    private JwtUserDetailsService jwtUserDetailsService;

    @Value("${jwt.secret}")
    private String signingSecret;

    private TokenDto createToken(String loginId) throws DemoApiException {
        UserAccountModel userAccountModel = userService.findAccountByLoginId(loginId);
        if (userAccountModel == null) {
            throw new DemoApiException("User not found: " + loginId, DemoError.USER_NOT_FOUND);
        }

        UserDetails userDetails = jwtUserDetailsService.loadUser(userAccountModel);
        String accessTokenCode = UUID.randomUUID().toString();
        String accessToken = JwtUtil.generateToken(userDetails, accessTokenCode, JwtUtil.TokenType.ACCESS, signingSecret);
        String accessTokenHash = passwordManager.getSha256Hash(accessToken);

        String refreshTokenCode = UUID.randomUUID().toString();
        String refreshToken = JwtUtil.generateToken(userDetails, refreshTokenCode, JwtUtil.TokenType.REFRESH, signingSecret);
        String refreshTokenHash = passwordManager.getSha256Hash(refreshToken);

        UserOAuth2TokenModel userOAuth2TokenModel = new UserOAuth2TokenModel(userAccountModel, accessTokenHash, refreshTokenHash);
        userService.save(userOAuth2TokenModel);

        return new TokenDto(accessToken, refreshToken);
    }

    private void authenticate(String loginId, String password) throws DemoApiException {
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

    public void deleteTokenByAccessToken(String accessToken) throws DemoApiException {
        String accessTokenHash = passwordManager.getSha256Hash(accessToken);
        UserOAuth2TokenModel userOAuth2TokenModel = userService.findOAuth2TokenByAccessToken(accessTokenHash);
        if (userOAuth2TokenModel == null) {
            throw new DemoApiException("Access Token not found.", DemoError.ACCESS_TOKEN_NOT_FOUND);
        }
        userOAuth2TokenModel.setDeleted(true);
        userService.save(userOAuth2TokenModel);
    }

    public TokenDto authenticateByPassword(String loginId, String password) throws DemoApiException {
        authenticate(loginId, password);
        return createToken(loginId);
    }

    public void authenticateByAccessToken(String token) throws DemoApiException {
        String tokenLoginId = null;
        String tokenHash = null;

        // Validate Access Token Structure, Expiration and retrieve info
        try {
            JwtUtil.TokenType tokenType = JwtUtil.getTokenTypeFromToken(token, signingSecret);
            if (JwtUtil.TokenType.ACCESS.equals(tokenType)) {
                tokenLoginId = JwtUtil.getUsernameFromToken(token, signingSecret);
                tokenHash = passwordManager.getSha256Hash(token);
            } else {
                logger.debug("Invalid Access Token Type: " + token);
            }
        } catch (IllegalArgumentException e) {
            logger.debug("Unable to get Access Token: " + token);
        } catch (ExpiredJwtException e) {
            throw new DemoApiException("Access Token Expired.", DemoError.ACCESS_TOKEN_EXPIRED);
        }

        // Skip DB validation if Token LoginID same as in Security Context
        boolean requireDbValidatation = false;
        if (tokenLoginId != null) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                requireDbValidatation = true;
            } else {
                UserAccountModel userAccountModel = (UserAccountModel) SecurityContextHolder.getContext().getAuthentication().getDetails();
                if (!tokenLoginId.equals(userAccountModel.getLoginId())) {
                    requireDbValidatation = true;
                }
            }
        }

        if (requireDbValidatation) {
            // Validate Token with DB record
            UserOAuth2TokenModel userOAuth2TokenModel = userService.findOAuth2TokenByAccessToken(tokenHash);
            if (userOAuth2TokenModel == null) {
                logger.debug("DB Token record not found: " + token);
                return;
            }

            // Manually set Spring Security authentication
            UserDetails userDetails = jwtUserDetailsService.loadUser(userOAuth2TokenModel.getUserAccountModel());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(userOAuth2TokenModel.getUserAccountModel());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }

    public TokenDto refreshToken(String token) throws DemoApiException {
        String tokenLoginId = null;
        String tokenHash = null;

        // Validate Refresh Token Structure, Expiration and retrieve info
        try {
            JwtUtil.TokenType tokenType = JwtUtil.getTokenTypeFromToken(token, signingSecret);
            if (JwtUtil.TokenType.REFRESH.equals(tokenType)) {
                tokenLoginId = JwtUtil.getUsernameFromToken(token, signingSecret);
                tokenHash = passwordManager.getSha256Hash(token);
            } else {
                logger.debug("Invalid Refresh Token Type: " + token);
            }
        } catch (IllegalArgumentException e) {
            logger.debug("Unable to get Refresh Token: " + token);
        } catch (ExpiredJwtException e) {
            throw new DemoApiException("Refresh Token Expired.", DemoError.REFRESH_TOKEN_EXPIRED);
        }

        // Validate Token with DB record
        UserOAuth2TokenModel userOAuth2TokenModel = userService.findOAuth2TokenByRefreshToken(tokenHash);
        if (userOAuth2TokenModel == null) {
            logger.debug("DB Token record not found: " + token);
            throw new DemoApiException("Refresh Token not found.", DemoError.REFRESH_TOKEN_NOT_FOUND);
        }

        // Create new set of AccessToken and RefreshToken
        TokenDto tokenDto = createToken(tokenLoginId);

        // Soft Delete original Token record
        userOAuth2TokenModel.setDeleted(true);
        userService.save(userOAuth2TokenModel);

        return tokenDto;
    }

}
