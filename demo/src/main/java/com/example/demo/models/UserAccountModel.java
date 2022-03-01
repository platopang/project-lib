package com.example.demo.models;

import com.example.demo.models.base.BaseModel;

import javax.persistence.*;

@Entity
@Table(name="user_account")
public class UserAccountModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name="login_id")
    private String loginId;

    @Column(name="hashed_password")
    private String hashedPassword;

    @Column(name="login_token")
    private String loginToken;

    @Column(name="invalid_login_attempt_count")
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
