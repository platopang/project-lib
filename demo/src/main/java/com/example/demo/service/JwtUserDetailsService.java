package com.example.demo.service;

import com.example.demo.models.UserAccountModel;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Resource
    private UserService userService;

    public UserDetails loadUser(@NonNull UserAccountModel userAccountModel) {
        String loginId = userAccountModel.getLoginId();
        String hashedPassword = userAccountModel.getHashedPassword();
        return User.builder()
                .username(loginId)
                .password(new BCryptPasswordEncoder().encode(hashedPassword))
                .roles("USER")
                .build();
    }

    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        if (loginId == null) throw new UsernameNotFoundException("User not found");

        UserAccountModel userAccountModel = userService.findAccountByLoginId(loginId);
        if (userAccountModel == null) {
            throw new UsernameNotFoundException("User not found: " + loginId);
        }
        return loadUser(userAccountModel);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
