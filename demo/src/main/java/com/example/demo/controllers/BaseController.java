package com.example.demo.controllers;

import com.example.demo.exceptions.DemoApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BaseController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler({DemoApiException.class})
    @ResponseBody
    public String handleLsServiceException(DemoApiException e) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(e);
    }

}
