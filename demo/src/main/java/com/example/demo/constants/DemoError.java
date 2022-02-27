package com.example.demo.constants;

public enum DemoError {

    // User Authentication
      BAD_CREDENTIALS(1101, "登入電郵地址或密碼不正確")
    , USER_AUTHENTICATION_FAILED(1102, "登入失敗")
    , USER_NOT_FOUND(1103, "找不到用戶")
    , CURRENT_USER_ACCOUNT_NOT_FOUND(1104, "登入狀態不正確")
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
