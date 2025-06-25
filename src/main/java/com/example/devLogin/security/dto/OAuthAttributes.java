package com.example.devLogin.security.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
  
  //OAuth2 로그인 시 전달받는 사용자 정보
  private Map<String, Object> attributes; //OAuth2 제공자에서 가져온 모든 사용자 속성
  private String nameAttributeKey; //사용자  식별에 사용할 키 이름
  private String name;
  private String email;
  private String picture;
  private String id; //OAuth 사용자 고유 ID -> 실제 유저 식별 값
  
  @Builder //모든 필드를 초기화 하는 빌더 생성자
  public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String id) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.picture = picture;
    this.id = id;
  }
  
  //OAuth 제공자 구분에 따라 처리할 메서드
  public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
    
    if("naver".equals(registrationId)) { //Naver로그인
      return ofNaver("id", attributes);
    }
    else if("kakao".equals(registrationId)) {
      return ofKakao("id", attributes);
    }
    else if("github".equals(registrationId)) {
      return ofGitHub("id", attributes);
    }
    
    return ofGoogle(userNameAttributeName, attributes); //기본값은 Google 로그인 처리
  }
  
  private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
    return com.example.devLogin.security.dto.OAuthAttributes.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .id((String) attributes.get(userNameAttributeName)) //사용자 ID
        .attributes(attributes) //전체 속성 Map
        .nameAttributeKey(userNameAttributeName) //사용자 식별 키
        .build();
  }
  
  private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>)attributes.get("response");
    
    return OAuthAttributes.builder()
        .name((String) response.get("name"))
        .email((String) response.get("email"))
        .picture((String) response.get("profile_image"))
        .id((String) response.get(userNameAttributeName))
        .attributes(response)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
  
  private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
    Long id = (Long)attributes.get("id");
    
    Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
    
    Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
    String nickname = (String)profile.get("nickname");
    String profileImageUrl = (String)profile.get("profile_image_url");
    
    String email = (String)kakaoAccount.get("email");
    
    return OAuthAttributes.builder()
        .name(nickname)
        .email(email)
        .picture(profileImageUrl)
        .id("" + id)
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
  
  private static OAuthAttributes ofGitHub(String userNameAttributeName, Map<String, Object> attributes) {
    String username = (String)attributes.get("login");
    Integer id = (Integer)attributes.get("id");
    String nickname = username;
    String profileImageUrl = (String)attributes.get("avatar_url");
    String email = (String)attributes.get("email");
    
    return OAuthAttributes.builder()
        .name(nickname)
        .email(email)
        .picture(profileImageUrl)
        .id("" + id)
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
}
