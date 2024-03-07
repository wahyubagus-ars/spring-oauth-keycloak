package com.ars.authservice.service;

import com.ars.authservice.domain.dto.BaseResponseDto;
import com.ars.authservice.domain.dto.LoginRequestDto;
import com.ars.authservice.domain.dto.TokenDto;
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

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";

    public ResponseEntity<Object> login(LoginRequestDto request, HttpServletResponse servletResponse) {
        log.info("Start to get access token");

        TokenDto tokenDto = this.getAccessToken(request);

        servletResponse.addHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        servletResponse.addHeader(EXPIRES_IN, String.valueOf(tokenDto.getExpiresIn()));

        /**
         * You can store the refresh token in a database such as Redis.
         */

        return new ResponseEntity<>(BaseResponseDto.builder().status("SUCCESS").build(), HttpStatus.OK);
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
}
