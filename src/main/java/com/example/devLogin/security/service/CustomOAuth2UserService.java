package com.example.devLogin.security.service;

import com.example.devLogin.entity.UserEntity;
import com.example.devLogin.respository.UserRepository;
import com.example.devLogin.security.dto.OAuthAttributes;
import com.example.devLogin.security.vo.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  
  @Autowired
  private UserRepository userRepository; //사용자 정보를 처리할 UserRepository 의존성 주입
  
  
  //OAuth2 로그인 시 사용자의 정보를 가져와서 CustomUser 객체로 반환하는 메서드
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("loadUser");
    
    //OAuth2 사용자 정보 제공 서비스 생성
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest); //사용자 정보 조회
    
    //클라이언트 등록 ID(google,naver,kakao 등)
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    
    //사용자 식별을 위한 키 이름 (Oauth 제공자마다 다름)
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUserNameAttributeName();
    
    log.info("loadUser registrationId = " + registrationId);
    log.info("loadUser userNameAttributeName = " + userNameAttributeName);
    
    //공급자로부터 받은 사용자 정보를 OAuthAttributes DTO로 매핑
    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
    
    String nameAttributeKey = attributes.getNameAttributeKey();
    String name = attributes.getName();
    String email = attributes.getEmail();
    String picture = attributes.getPicture();
    String id = attributes.getId();
    String socialType = "";
    
    if("naver".equals(registrationId)) {
      socialType = "naver";
    }
    else if("kakao".equals(registrationId)) {
      socialType = "kakao";
    }
    else if("github".equals(registrationId)) {
      socialType = "github";
      
      //github는 이메일이 없을 수 있어서 한 번 더 호출
      if(email == null) {
        log.info("loadUser userRequest.getAccessToken().getTokenValue() = " + userRequest.getAccessToken().getTokenValue());
        
        email = getEmailFromGitHub(userRequest.getAccessToken().getTokenValue());
        
        log.info("loadUser GitHub email = " + email);
      }
    }
    else {
      socialType = "google";
    }
    
    log.info("loadUser nameAttributeKey = " + nameAttributeKey);
    log.info("loadUser id = " + id);
    log.info("loadUser socialType = " + socialType);
    log.info("loadUser name = " + name);
    log.info("loadUser email = " + email);
    log.info("loadUser attributes = " + attributes);
    
    //null 방지 처리
    if(name == null) name = "";
    if(email == null) email = "";

    
    //권한 목록 생성(기본 권한 ROLE_USER 부여)
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    authorities.add(authority);
    
    String username = email; //사용자명으로 이메일 사용
    String authProvider = socialType; //OAuth 제공자 정보
    UserEntity userEntity = null; // 사용자 정보 저장 객체
    
    //사용자 정보가 db에 없으면 신규저장, 있으면 조회
    if(!userRepository.existsByUsername(username)) {
      userEntity = UserEntity.builder()
          .username(username)
          .authProvider(authProvider)
          .build();
      userEntity = userRepository.save(userEntity); //Db에 저장
    } else {
      userEntity = userRepository.findByUsername(username); //기존 사용자 조회
    }
    
    log.info("Successfully pulled user info username {} authProvider {}", username, authProvider);
    
    //CustomUser객체 반환하여 SecurityContext에 등록되도록함
    return new CustomUser(userEntity.getId(), email, name, authorities, attributes);
  }
  
  private String getEmailFromGitHub(String accessToken) {
    String url = "https://api.github.com/user/emails"; //github 이메일 API
    
    RestTemplate restTemplate = new RestTemplate(); //HTTP 요청 도구
    
    //헤더 추가
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Accept", "application/vnd.github.v3+json");
    
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
    
    List<Map<String, Object>> emails = response.getBody();
    
    if (emails != null) {
      for (Map<String, Object> emailData : emails) {
        if ((Boolean) emailData.get("primary")) {
          return (String) emailData.get("email");
        }
      }
    }
    
    return null;
  }
  
}