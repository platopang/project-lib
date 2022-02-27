package com.example.demo.utils;

import com.example.demo.models.UserAccountModel;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {

    public static Optional<UserAccountModel> getCurrentUserAccount() {
        SecurityContext context = SecurityContextHolder.getContext();
        UserAccountModel userAccountModel = null;

        if (context != null &&
                context.getAuthentication() != null &&
                !"AnonymousUser".equalsIgnoreCase(context.getAuthentication().getPrincipal().toString())) {
            userAccountModel = (UserAccountModel) context.getAuthentication().getDetails();
        }
            return Optional.ofNullable(userAccountModel);
    }
}
