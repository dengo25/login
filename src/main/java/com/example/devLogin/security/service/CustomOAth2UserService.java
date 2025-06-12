package com.example.devLogin.security.service;


import com.example.devLogin.entity.User;
import com.example.devLogin.repository.UserRepository;
import com.example.devLogin.security.dto.OAuthAttributes;
import com.example.devLogin.security.vo.CustomOAth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
/* OAuth2 로그인 후 사용자 정보를 처리하는 커스텀 CustomOAth2UserService구현 클래스
   1. 소셜로그인 과정에서 사용자 정보를 가져오고 가공하는 핵심 서비스 역할
     대표적으로 외부 인증 제공자에서 사용자 정보를 받아온다.(이메일, 프로필 등)
   2. 받은 사용자 정보를 OAthAttributes로 변환한다, 소셜 마다 다르기 때문에 각각 분기해서 처리
   3. 사용자 이메일을 기준으로 사용자 등록 처리
   4. CustomOAuth2User 객체를 생성해서 반환, 이 객체는 spring security내부에서 인증된 사용자 정보를 담는 역할
   
*/
public class CustomOAth2UserService implements OAuth2UserService<OAuth2UserRequest,OAuth2User> {
  
  private final UserRepository userRepository;
  
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  
  
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    
    log.info("loadUser");
    
    //기본 OAuth2UserService를 통해 로그인된 사용자의 정보를 받아온다.
    // 소셜 로그인에 성공하면 토큰을 기반으로 사용자 정보가 json으로 넘어오고 그걸 OAuth2User객체로 변환
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    
    
    //Oath2 서비스 등록 -> 어떤 플랫폼으로 로그인 했는지 구분
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    
    //사용자 식별 키(google은 sub, github는 id)
    String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
        .getUserInfoEndpoint().getUserNameAttributeName();
    
    log.info("loadUser registrationId = " + registrationId);
    log.info("loadUser userNameAttributeName = " + userNameAttributeName);
    
    //OAuthAttributes라는 DTO 객체로 사용자 정보 매핑
    OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
    
    //속성 정보 추출
    String nameAttributeKey = attributes.getNameAttributeKey(); //사용자 식별 키 이름
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
      
      //github는 기본 제공 정보에 이메일이 없을 수도 있어 api를 통해 수동 조회
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
    log.info("loadUser picture = " + picture);
    log.info("loadUser attributes = " + attributes);
    
    // null방지를 위한 기본값 처리
    if(name == null) name = "";
    if(email == null) email = "";
    
    
    // 기본 권한 부여 기본적으로 (소셜 로그인 사용자는 일반 사용자)
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); //일반 사용자 권한
    authorities.add(authority);
    
    // 이메일로 사용자 존재 여부 조회
    Optional<User> optionalUser = userRepository.findByUsername(email);
    
    User createdUser = null;
    
    //존재하지 않으면 새 사용자 생성 및 저장
    if(optionalUser.isEmpty()) {
      User user = new User();
      user.setUsername(email);
      user.setPassword(passwordEncoder.encode("1234")); //(비밀번호는 임시로 저장)
      user.setSocialId(id); //소셜 서비스에서 받은 사용자 고유 ID
      user.setSocialType(socialType);
      
      createdUser = userRepository.save(user);
    }
    else {
      createdUser = optionalUser.orElseThrow();
    }
    
    //DB에 저장된 사용자 ID
    Long userId = createdUser.getId();
    
    // 커스텀 OAuth2USer 객체 반환(spring security에서 세션에 저장됨)
    return new CustomOAth2User(userId, email, name, authorities, attributes);
  }
  
  private String getEmailFromGitHub(String accessToken) {
    String url = "https://api.github.com/user/emails";
    
    RestTemplate restTemplate = new RestTemplate();
    
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
