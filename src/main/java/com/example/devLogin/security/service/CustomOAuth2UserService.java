package com.example.devLogin.security.service;

import com.example.devLogin.entity.UserEntity;
import com.example.devLogin.respository.UserRepository;
import com.example.devLogin.security.dto.OAuthAttributes;
import com.example.devLogin.security.vo.CustomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
  
  @Autowired
  private UserRepository userRepository;
  
  
  //OAuth2 로그인 시 사용자의 정보를 가져와서 CustomUser 객체로 반환하는 메서드
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    log.info("loadUser");
    
    //OAuth2 사용자 정보 제공 서비스 생성
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest); //사용자 정보 조회
    
    //OAuth 공급자 이름
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    
    //사용자 식별을 위한 키 이름
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
    String socialType = "google";
    
    log.info("loadUser nameAttributeKey = " + nameAttributeKey);
    log.info("loadUser id = " + id);
    log.info("loadUser socialType = " + socialType);
    log.info("loadUser name = " + name);
    log.info("loadUser email = " + email);
    log.info("loadUser picture = " + picture);
    
    log.info("loadUser attributes = " + attributes);
    
    if(name == null) name = "";
    if(email == null) email = "";
    
    //권한 목록 생성(기본 권한 ROLE_USER 부여)
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    authorities.add(authority);
    
    String username = email; //사용자명으로 이메일 사용
    String authProvider = socialType; //OAuth 제공자 정보
    UserEntity userEntity = null; // 사용자 정보 저장 객체
    
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
    
    return new CustomUser(userEntity.getId(), email, name, authorities, attributes);
  }
  

  
}