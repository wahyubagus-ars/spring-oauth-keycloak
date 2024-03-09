package com.ars.authservice.controller;

import com.ars.authservice.domain.dto.LoginRequestDto;
import com.ars.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto request, HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse) {
        return authService.login(request, servletRequest, servletResponse);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<Object> refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return authService.refreshToken(servletRequest, servletResponse);
    }
}
