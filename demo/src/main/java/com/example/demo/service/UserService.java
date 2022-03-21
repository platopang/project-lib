package com.example.demo.service;

import com.example.demo.constants.DemoError;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.models.UserAccountModel;
import com.example.demo.models.UserOAuth2TokenModel;
import com.example.demo.repositories.UserAccountRepository;
import com.example.demo.repositories.UserOAuth2TokenRepository;
import com.example.demo.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserAccountRepository userAccountRepository;
    @Resource
    private UserOAuth2TokenRepository userOAuth2TokenRepository;

    public UserAccountModel findAccountByLoginId(String loginId) {
        return userAccountRepository.findByLoginId(loginId);
    }

    public UserOAuth2TokenModel findOAuth2TokenByAccessToken(String accessToken) {
        return userOAuth2TokenRepository.findByAccessTokenAndDeleted(accessToken, false);
    }

    public UserOAuth2TokenModel findOAuth2TokenByRefreshToken(String refreshToken) {
        return userOAuth2TokenRepository.findByRefreshTokenAndDeleted(refreshToken, false);
    }

    public UserAccountModel getCurrentUserAccount() throws DemoApiException {
        UserAccountModel userAccountModel = SecurityUtil.getCurrentUserAccount().orElse(null);
        if (userAccountModel == null) {
            throw new DemoApiException("Current User Account not found.", DemoError.CURRENT_USER_ACCOUNT_NOT_FOUND);
        }

        return userAccountModel;
    }

    public UserAccountModel save(UserAccountModel userAccountModel) {
        return userAccountRepository.save(userAccountModel);
    }

    public UserOAuth2TokenModel save(UserOAuth2TokenModel userOAuth2TokenModel) {
        return userOAuth2TokenRepository.save(userOAuth2TokenModel);
    }

}
