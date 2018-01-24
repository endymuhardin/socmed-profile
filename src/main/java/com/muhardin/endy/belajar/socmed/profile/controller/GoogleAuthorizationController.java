package com.muhardin.endy.belajar.socmed.profile.controller;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.muhardin.endy.belajar.socmed.profile.dao.WebsiteDao;
import com.muhardin.endy.belajar.socmed.profile.entity.Website;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Controller
@RequestMapping("/google")
public class GoogleAuthorizationController {

    @Value("${google.oauth.client-id}") private String clientId;
    @Value("${google.oauth.client-secret}") private String clientSecret;
    @Value("${google.oauth.scope}") private String oauthScope;

    @Autowired private WebsiteDao websiteDao;

    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void initGoogleAuth() {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                clientId, clientSecret,
                Collections.singleton(oauthScope)).setAccessType("offline")
                .build();
    }

    @GetMapping("/auth/start")
    public String startAuthorizationFlow(HttpServletRequest request) {
        String url = flow.newAuthorizationUrl()
                .setState("abc")
                .setRedirectUri(redirectUrl(request))
                .build();
        return "redirect:"+url;
    }

    @GetMapping("/auth/callback")
    public String handleCallbackFromGoogle(@RequestParam String state, @RequestParam String code, HttpServletRequest request)  {
        try {
            TokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUrl(request))
                    .execute();
            Website web = new Website();
            web.setUrl("https://software.endy.muhardin.com");
            web.setGoogleAnalyticsAccountId("UA-36102948-1");
            web.setGoogleAnalyticsViewId("65778193");
            web.setAccessToken(tokenResponse.getAccessToken());
            web.setRefreshToken(tokenResponse.getRefreshToken());
            web.setTokenExpireDate(Date.from(LocalDateTime.now()
                    .plusSeconds(tokenResponse.getExpiresInSeconds())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()));
            websiteDao.save(web);
            return "redirect:/website/analytics";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String redirectUrl(HttpServletRequest request) {
        return request.getRequestURL().toString()
                .replace(request.getRequestURI(), request.getContextPath())
                +"/google/auth/callback";
    }
}