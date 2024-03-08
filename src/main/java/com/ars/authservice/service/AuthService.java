package com.ars.authservice.service;

import com.ars.authservice.domain.dto.BaseResponseDto;
import com.ars.authservice.domain.dto.LoginRequestDto;
import com.ars.authservice.domain.dto.TokenDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${keycloak.client-id}")
    private String kcClientId;

    @Value("${keycloak.client-secret}")
    private String kcClientSecret;

    @Value("${keycloak.get-token-url}")
    private String kcGetTokenUrl;

    @Value("${keycloak.logout-url}")
    private String kcLogoutUrl;

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String ACCESS_TOKEN = "Access-Token";
    private static final String REFRESH_TOKEN = "Refresh-Token";
    private static final String EXPIRES_IN = "Expires-In";

    public ResponseEntity<Object> login(LoginRequestDto request, HttpServletResponse servletResponse) {
        log.info("Start to get access token");

        TokenDto tokenDto = this.getAccessToken(request);

        servletResponse.addHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        servletResponse.addHeader(REFRESH_TOKEN, tokenDto.getRefreshToken());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(tokenDto.getExpiresIn()));

        /**
         * You can store the refresh token in a database such as Redis.
         */

        return ResponseEntity.ok().body(BaseResponseDto.builder()
                .status("SUCCESS")
                .build());
    }

    public ResponseEntity<Object> refreshToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        log.info("Start to refresh access token");

        String refreshToken = servletRequest.getHeader("refresh_token");

        TokenDto tokenDto = this.getRefreshToken(refreshToken);

        servletResponse.addHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(tokenDto.getExpiresIn()));

        /**
         * You can store the refresh token in a database such as Redis.
         */

        return ResponseEntity.ok().body(BaseResponseDto.builder()
                .status("SUCCESS")
                .data(tokenDto)
                .build());
    }

    private TokenDto getAccessToken(LoginRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", GRANT_TYPE_PASSWORD);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);
        requestBody.add("username", request.getUsername());
        requestBody.add("password", request.getPassword());

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl,
                new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }

    private TokenDto getRefreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", kcClientId);
        requestBody.add("client_secret", kcClientSecret);

        ResponseEntity<TokenDto> response = restTemplate.postForEntity(kcGetTokenUrl,
                new HttpEntity<>(requestBody, headers), TokenDto.class);

        return response.getBody();
    }
}
