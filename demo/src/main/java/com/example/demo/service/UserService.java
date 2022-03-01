package com.example.demo.service;

import com.example.demo.constants.DemoError;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.models.UserAccountModel;
import com.example.demo.repositories.UserAccountRepository;
import com.example.demo.securities.PasswordManager;
import com.example.demo.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    protected UserAccountRepository userAccountRepository;

    public UserAccountModel findAccountByLoginId(String loginId) {
        return userAccountRepository.findByLoginId(loginId);
    }

    public UserAccountModel getCurrentUserAccount() throws DemoApiException {
        UserAccountModel userAccountModel = SecurityUtil.getCurrentUserAccount().orElse(null);
        if (userAccountModel == null) {
            throw new DemoApiException("Current User Account not found.", DemoError.CURRENT_USER_ACCOUNT_NOT_FOUND);
        }

        return userAccountModel;
    }

    public UserAccountModel save(UserAccountModel userAccountModel) {
        // ToDo: implement DAO
        return userAccountModel;
    }

}
