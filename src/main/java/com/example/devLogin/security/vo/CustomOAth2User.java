package com.example.devLogin.security.vo;

import com.example.devLogin.security.dto.OAuthAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

//이 클래스는 스프링 스큐리티에서 DefaultOAuth2User 상속 후 추가 사용자 정보를 확장한 커스텀 사용자 객체
//DefaultOAuth2User는 Oath 로그인 시 사용자 정보를 담아주는 클래스
public class CustomOAth2User extends DefaultOAuth2User {
  
  private static final long serialVersionUID = 1L;
  
  private Long userId; //사용자 Id (DB의 UserId와 연동)
  private String username; // 이름 또는 이메일
  private String nickname;
  
  // 커스텀 Oath2 사용자 객체 생성자
  public CustomOAth2User(Long userId, //사용자 고유ID
                         String username, //사용자 이름
                         String nickname, //사용자 닉네임
                         Collection<? extends GrantedAuthority> authorities, //로그인한 사용자 권한 목록
                         OAuthAttributes attributes) { //Oath로부터 전달 받은 속성 정보
    
    //부모 클래스인 DefaultOAuth2User의 생성자 호출
    //getAttribute() 사용자 정보 map,  getNameAttributeKey() 식별 키
    super(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());
    
    this.userId = userId;
    this.username = username;
    this.nickname = nickname;
  }
  
  //DefaultOAuth2User의 getName() 오버라이딩 -> 사용자명 반환
  @Override
  public String getName() {
    return super.getName();
  }
  
  public Long getUserId() {
    return userId;
  }
  
  public String getUsername() {
    return username;
  }
  
  public String getNickname() {
    return nickname;
  }
}
