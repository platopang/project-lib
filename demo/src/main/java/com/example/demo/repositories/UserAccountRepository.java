package com.example.demo.repositories;

import com.example.demo.models.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccountModel, Long> {

    UserAccountModel findByLoginId(String loginId);

}
