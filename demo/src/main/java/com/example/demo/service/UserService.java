package com.example.demo.service;

import com.example.demo.constants.DemoError;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.models.UserAccountModel;
import com.example.demo.securities.PasswordManager;
import com.example.demo.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    protected PasswordManager passwordManager;

    public UserAccountModel findAccountByLoginId(String loginId) {
        // ToDo: implement DAO
        // **** Temp code for demo - START
        UserAccountModel userAccountModel = new UserAccountModel();
        userAccountModel.setId(1L);
        userAccountModel.setLoginId(loginId);
        userAccountModel.setHashedPassword(passwordManager.getHashedPassword(1L, "P@ssw0rd"));
        userAccountModel.setInvalidLoginAttemptCount(0);
        // **** Temp code for demo - END
        return userAccountModel;
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
