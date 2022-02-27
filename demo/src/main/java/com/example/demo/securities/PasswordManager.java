package com.example.demo.securities;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class PasswordManager {

    public String getHashedPassword(long uid, String plainPassword) {
        return DigestUtils.sha256Hex(uid + plainPassword);
    }

    public String getSha256Hash(String plainText) {
        return DigestUtils.sha256Hex(plainText);
    }

}
