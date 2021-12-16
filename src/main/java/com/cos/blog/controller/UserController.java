package com.cos.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class UserController {
  
  @Value("${cos.key}")
  private String cosKey;
  
  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private UserService userService;

  @GetMapping("/auth/joinForm")
  public String joinFrom() {

    return "user/joinForm";
  }

  @GetMapping("/auth/loginForm")
  public String loginForm() {

    return "user/loginForm";
  }

  @GetMapping("/auth/kakao/callback")
  public String kakaoCallback(String code) {
    RestTemplate rt = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", "cc5750ae21ed41e383e38a2cdcddf6ae");
    params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

    ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
        kakaoTokenRequest, String.class);

    ObjectMapper objectMapper = new ObjectMapper();

    OAuthToken oAuthToken = null;
    try {
      oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println(oAuthToken.getAccess_token());

    RestTemplate rt2 = new RestTemplate();
    HttpHeaders headers2 = new HttpHeaders();
    headers2.add("Authorization", "Bearer " + oAuthToken.getAccess_token());
    headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);

    ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
        kakaoProfileRequest2, String.class);

    System.out.println(response2.getBody());

    ObjectMapper objectMapper2 = new ObjectMapper();

    KakaoProfile kakaoProfile = null;
    try {
      kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
    System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());
    System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
    System.out.println("블로그서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());
    System.out.println("블로그서버 패스워드 : " + cosKey);

    User kakaoUser = User.builder()
        .username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
        .password(cosKey)
        .email(kakaoProfile.getKakao_account().getEmail())
        .oauth("kakao")
        .build();
    
    //신규 또는 기존 회원인지 구분 체키 처리
    System.out.println(kakaoUser.getUsername());
    User originUser = userService.회원찾기(kakaoUser.getUsername());
    
    if (originUser.getUsername() == null) {
      System.out.println("기존 회원이 아닙니다................!!");
      userService.회원가입(kakaoUser);
    }
    
    //로그인 처리
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), cosKey));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    
    return "redirect:/";
  }

  @GetMapping("/user/updateForm")
  public String updateForm() {
    return "user/updateForm";
  }
}