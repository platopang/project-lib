package com.example.demo.exceptions;

import com.example.demo.constants.DemoError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"stackTrace", "cause", "suppressed", "localizedMessage"})
public class DemoApiException extends Exception {

    private final int code;
    private final String description;
    private final String message;

    public DemoApiException(String message, Throwable cause, DemoError demoErrEnum) {
        super(message, cause);
        this.message = message;
        this.code = demoErrEnum.getCode();
        this.description = demoErrEnum.getDescription();
    }

    public DemoApiException(String message, DemoError demoErrEnum) {
        super(message);
        this.message = message;
        this.code = demoErrEnum.getCode();
        this.description = demoErrEnum.getDescription();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
