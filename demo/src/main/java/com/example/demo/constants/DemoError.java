package com.example.demo.constants;

public enum DemoError {

    // User Authentication
      BAD_CREDENTIALS(1101, "登入電郵地址或密碼不正確")
    , USER_AUTHENTICATION_FAILED(1102, "登入失敗")
    , USER_NOT_FOUND(1103, "找不到用戶")
    , CURRENT_USER_ACCOUNT_NOT_FOUND(1104, "登入狀態不正確")

    // Token Validation
    , ACCESS_TOKEN_EXPIRED(1201, "存取金鑰已失效")
    , REFRESH_TOKEN_EXPIRED(1202, "再生金鑰已失效")
    , ACCESS_TOKEN_NOT_FOUND(1203, "存取金鑰找不到")
    , REFRESH_TOKEN_NOT_FOUND(1204, "再生金鑰找不到")

    ;

    private final int code;
    private final String description;

    DemoError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }

}
