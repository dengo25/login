package com.example.devLogin.security.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
//Oauth 인증 후 반환된 사용자 정보를 담는 DTO zmffotm
public class OAuthAttributes {
  
  private Map<String ,Object> attributes; //Oauth 제공자로부터 전달 받은 사용자 정보 전체 Map
  private String nameAttributeKey; //Oauth 사용자 식별 키
  private String name;
  private String email;
  private String picture;
  private String id; //사용자 고유 id
  
  @Builder
  public OAuthAttributes(Map<String, Object> attributes, //전체 속성 Map
                         String nameAttributeKey, //식별 키 이름
                         String name,
                         String email,
                         String picture, //프로필 이미지
                         String id) { //사용자 ID
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.picture = picture;
    this.id = id;
  }
  
  // OAuthAttributes 객체를 생성하는 정적 팩토리 메서드
  // 현재는 Google,Naver
  public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                   Map<String, Object> attributes) {

    if("naver".equals(registrationId)) {
      return ofNaver("id", attributes);
    }
    else if("kakao".equals(registrationId)) {
      
      return ofKakao("id", attributes);
    }
    else if("github".equals(registrationId)) {
      return ofGitHub("id", attributes);
    }
    
    return ofGoogle(userNameAttributeName, attributes);
  }
  
  // Google 로그인 전용 사용자 정보 매핑 메서드
  private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
    return OAuthAttributes.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .id((String) attributes.get(userNameAttributeName))
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
  
  //naver 로그인 전용 사용자 정보 매핑 메서드
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
  
  //kakao 로그인 전용 사용자 정보 매핑 메서드
  private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
    Long id = (Long)attributes.get("id");
    
    //계정 정보
    //카카오는 중첩 구조이기 떄문에 map으로 계정 꺼내줘야한다.
    Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
    
    //프로필 객체에서 이름 및 프로필 이미지 추출
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
  
  
  //github 로그인 전용 사용자 정보 매핑 메서드
  private static OAuthAttributes ofGitHub(String userNameAttributeName, Map<String, Object> attributes) {
    String username = (String)attributes.get("login");
    Integer id = (Integer)attributes.get("id");
    String profileImageUrl = (String)attributes.get("avatar_url");
    String email = (String)attributes.get("email");
    
    String nickname = username; //github는 닉네임이 별도로 없으므로 login을 닉네임으로 사용
    
    return OAuthAttributes.builder()
        .name(nickname)
        .email(email)
        .picture(profileImageUrl)
        .id("" + id) //id는 문자열로 변환하여 저장
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }
}
