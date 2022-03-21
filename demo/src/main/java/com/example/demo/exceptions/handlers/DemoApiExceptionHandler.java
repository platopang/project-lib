package com.example.demo.exceptions.handlers;

import com.example.demo.exceptions.DemoApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DemoApiExceptionHandler {

    @ExceptionHandler({DemoApiException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ResponseBody
    public String handleServiceException(DemoApiException e) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(e);
    }

}
