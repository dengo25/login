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
  
  //OAuth 제공자 구분에 따라 처리할 메서드 (현재는 Google만 지원)
  public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
    
    return ofGoogle(userNameAttributeName, attributes);
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
  
/*  google에서 제공하는 json 형태
  {
    "sub":"123124124",
    "name":"김무개",
    "email":"test@test.com",
    "picture":"http://profile.jpg"
  }*/
}
