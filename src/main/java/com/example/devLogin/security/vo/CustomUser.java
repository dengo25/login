package com.example.devLogin.security.vo;

import com.example.devLogin.security.dto.OAuthAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/*
  Spring Security의 DefaultOAuth2User를 확장하여 사용자 id, 이메일, 사용자명을 추가로 보관
  CustomUser 클래스는 OAuth2 로그인으로 받아온 사용자 정보 중에서
  우리가 자주사용하는 값들만 따로 필드로 꺼내 관리하기위해서
*/
public class CustomUser extends DefaultOAuth2User {
  
  
  private static final long serialVersionUID = 1L;
  
  private Long id;
  private String email;
  private String username;
  
  //DefaultOAuth2User클래스는
  //Map<String,Object> String email=(String) attribute.get("email")형식으로 갖고 있다.
  
  //OAuth2 인증 정보를 기반으로 CustomUser 객체 생성
  public CustomUser(Long id,
                    String email,
                    String username,
                    Collection<? extends GrantedAuthority> authorities,
                    OAuthAttributes attributes) { //OAuth 사용자 정보 DTO
    
    //부모 클래스인 DefaulteOAuth2User의 생성자 호출
    //attributes.getAttributes() -> 속성 맵
    //attributes.getNameAttributeKey() 이름 식별 키
    super(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());
    
    this.id = id;
    this.email = email;
    this.username = username;
  }
  
  public String getEmail() {
    return email;
  }
  
  public String getUsername() {
    return username;
  }
  
  @Override
  public String getName() {
    return "" + this.id;
  }
  
}
