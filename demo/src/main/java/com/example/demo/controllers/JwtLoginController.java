package com.example.demo.controllers;

import com.example.demo.dtos.JwtLoginDto;
import com.example.demo.dtos.JwtRefreshTokenDto;
import com.example.demo.dtos.TokenDto;
import com.example.demo.exceptions.DemoApiException;
import com.example.demo.securities.JwtAuthenticator;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({ "/api" })
public class JwtLoginController {

    @Resource
    private JwtAuthenticator jwtAuthenticator;

    @PostMapping(value = "/login")
    public TokenDto login(@RequestBody JwtLoginDto jwtLoginDto) throws DemoApiException {
        return jwtAuthenticator.authenticateByPassword(jwtLoginDto.getLoginId(), jwtLoginDto.getPassword());
    }

    @PostMapping(value = "/refreshToken")
    public TokenDto refreshToken(@RequestBody JwtRefreshTokenDto jwtRefreshTokenDto) throws DemoApiException {
        return jwtAuthenticator.refreshToken(jwtRefreshTokenDto.getRefreshToken());
    }

    @GetMapping(value = "/healthCheck")
    public String healthCheck() {
        return "Success!";
    }
}
