package com.example.demo.models;

import com.example.demo.models.base.BaseModel;

import javax.persistence.*;

@Entity
@Table(name="user_oauth2_token")
public class UserOAuth2TokenModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @JoinColumn(name="user_account_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserAccountModel userAccountModel;

    @Column(name="access_token")
    private String accessToken;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="deleted")
    private Boolean deleted;

    public UserOAuth2TokenModel(){}

    public UserOAuth2TokenModel(UserAccountModel userAccountModel, String accessToken, String refreshToken) {
        this.userAccountModel = userAccountModel;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.deleted = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserAccountModel getUserAccountModel() {
        return userAccountModel;
    }

    public void setUserAccountModel(UserAccountModel userAccountModel) {
        this.userAccountModel = userAccountModel;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
