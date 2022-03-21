package com.example.demo.repositories;

import com.example.demo.models.UserOAuth2TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOAuth2TokenRepository extends JpaRepository<UserOAuth2TokenModel, Long> {

    UserOAuth2TokenModel findByAccessTokenAndDeleted(String accessToken, Boolean deleted);

    UserOAuth2TokenModel findByRefreshTokenAndDeleted(String refreshToken, Boolean deleted);

}
