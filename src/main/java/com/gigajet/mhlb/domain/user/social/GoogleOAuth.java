package com.gigajet.mhlb.domain.user.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigajet.mhlb.domain.user.dto.GoogleOAuthTokenRequestDto;
import com.gigajet.mhlb.domain.user.dto.GoogleUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth implements SocialOAuth {

    @Value("${OAuth2.google.url}")
    private String googleLoginUrl;
    @Value("${OAuth2.google.client-id}")
    private String googleClientId;
    @Value("${OAuth2.google.callback-url}")
    private String googleCallbackUrl;
    @Value("${OAuth2.google.client-secret}")
    private String googleClientSecret;
    @Value("${OAuth2.google.scope}")
    private String googleDataAccessScope;

    private final RestTemplate restTemplate;

    @Override
    public String getOAuthRedirectURL() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("scope", googleDataAccessScope);
        paramMap.put("response_type", "code");
        paramMap.put("client_id", googleClientId);
        paramMap.put("redirect_uri", googleCallbackUrl);

        String parameters = paramMap.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        String redirectURL = googleLoginUrl + "?" + parameters;
        log.info(redirectURL);

        return redirectURL;
    }

    public GoogleOAuthTokenRequestDto getAccessToken(String code) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        paramMap.put("client_id", googleClientId);
        paramMap.put("client_secret", googleClientSecret);
        paramMap.put("redirect_uri", googleCallbackUrl);
        paramMap.put("grant_type", "authorization_code");

        ResponseEntity<GoogleOAuthTokenRequestDto> responseEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token", paramMap, GoogleOAuthTokenRequestDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }

        return null;
    }

    public GoogleUserDto getUserInfo(GoogleOAuthTokenRequestDto googleOAuthTokenRequestDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", googleOAuthTokenRequestDto.getToken_type() + " " + googleOAuthTokenRequestDto.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<GoogleUserDto> responseEntity = restTemplate.exchange("https://www.googleapis.com/oauth2/v1/userinfo", HttpMethod.GET, request, GoogleUserDto.class);

        return responseEntity.getBody();
    }

}
