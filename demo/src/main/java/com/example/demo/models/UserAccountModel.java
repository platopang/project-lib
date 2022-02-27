package com.example.demo.models;

import com.example.demo.models.base.BaseModel;

public class UserAccountModel extends BaseModel {

    private long id;

    private String loginId;

    private String hashedPassword;

    private String loginToken;

    private Integer invalidLoginAttemptCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Integer getInvalidLoginAttemptCount() {
        return invalidLoginAttemptCount;
    }

    public void setInvalidLoginAttemptCount(Integer invalidLoginAttemptCount) {
        this.invalidLoginAttemptCount = invalidLoginAttemptCount;
    }

}
