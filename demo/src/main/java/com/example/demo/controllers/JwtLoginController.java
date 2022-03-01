package com.example.demo.controllers;

import com.example.demo.dtos.DataDto;
import com.example.demo.dtos.JwtLoginDto;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.securities.JwtAuthenticator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({ "/api" })
public class JwtLoginController extends BaseController {

    @Resource
    JwtAuthenticator jwtAuthenticator;

    @PostMapping(value = "/login")
    public DataDto login(@RequestBody JwtLoginDto jwtLoginDto) throws DemoApiException {
        String token = jwtAuthenticator.createToken(jwtLoginDto.getLoginId(), jwtLoginDto.getPassword());
        return new DataDto(token);
    }

    @GetMapping(value = "/logout")
    public void logout() throws DemoApiException {
        jwtAuthenticator.deleteToken();
    }

    @GetMapping(value = "/healthCheck")
    public String healthCheck() {
        return "Success!";
    }
}
