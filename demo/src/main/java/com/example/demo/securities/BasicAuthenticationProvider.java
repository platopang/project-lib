package com.example.demo.securities;

import com.example.demo.models.UserAccountModel;
import com.example.demo.service.UserService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {

    public static final int INVALID_LOGIN_ATTEMPT_LIMIT = 5;

    @Resource
    protected PasswordManager passwordManager;

    @Resource
    protected UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserAccountModel userAccountModel = userService.findAccountByLoginId(name);
        if (userAccountModel == null) {
            throw new BadCredentialsException("Username or password not match");
        }
        boolean isVerifiedUser = verifyUser(userAccountModel, password);

        if (isVerifiedUser) {
            ArrayList<SimpleGrantedAuthority> grantedAuthorities = grantAuthorities(userAccountModel);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    name, password, grantedAuthorities);

            setDetails(userAccountModel, token);

            return token;
        } else {
            throw new BadCredentialsException("Username or password not match");
        }
    }

    protected boolean verifyUser(UserAccountModel userAccountModel, String password) {
        int invalidLoginAttemptLimit = getInvalidLoginAttemptLimit();

        if (invalidLoginAttemptLimit <= userAccountModel.getInvalidLoginAttemptCount()) {
            throw new AuthenticationCredentialsNotFoundException("Account was locked due to repetitive invalid login attempts.");
        }

        if (userAccountModel.getHashedPassword().equals(passwordManager.getHashedPassword(userAccountModel.getId(), password))) {
            return true;
        } else {
            throw new BadCredentialsException("Username or password not match");
        }
    }

    protected int getInvalidLoginAttemptLimit() {
        return INVALID_LOGIN_ATTEMPT_LIMIT;
    }

    protected ArrayList<SimpleGrantedAuthority> grantAuthorities(UserAccountModel userAccountModel) {
        // To be overridden: grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userAccountModel.getType().name()));
        return new ArrayList<>();
    }

    protected void setDetails(UserAccountModel userAccountModel, UsernamePasswordAuthenticationToken token) {
        token.setDetails(userAccountModel);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
