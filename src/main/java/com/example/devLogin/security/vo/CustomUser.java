package com.example.devLogin.security.vo;

import com.example.devLogin.security.dto.OAuthAttributes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

//일반 로그인과 Oauth2 로그인을 모두 처리할 수 있는 사용자 인증 객체
public class CustomUser implements UserDetails, OAuth2User {
  
  private static final long serialVersionUID = 1L;
  
  private Long userId; //사용자 Id (DB의 UserId와 연동)
  private String username; // 이름 또는 이메일
  private String password;  // 일반 로그인 사용 시 필요
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;  // OAuth2 사용자 정보
  
  // 일반 로그인 사용자 생성자
  public CustomUser(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.attributes = null;  // 일반 로그인 사용자는 OAuth2 속성이 없으므로 null
  }
  
  
  // 커스텀 Oath2 사용자 객체 생성자
  public CustomUser(Long userId, //사용자 고유ID
                    String username, //사용자 이름
                    Collection<? extends GrantedAuthority> authorities, //로그인한 사용자 권한 목록
                    OAuthAttributes oAuthAttributes) { //Oath로부터 전달 받은 속성 정보
    this.userId = userId;
    this.username = username; //사용자 이메일 설정
    this.authorities = authorities;
    this.attributes = oAuthAttributes.getAttributes(); //Oauth2에서 받은 속성 정보 저장
    this.password = null;  // OAuth2 사용자는 비밀번호가 없음
  }
  
  @Override
  public String getName() {
    return username;  // Oauth2User에서 사용하는 고유 사용자명 반환(Spring Security용)
  }
  
  @Override //OAuth2 사용자 정보 반환
  public Map<String, Object> getAttributes() {
    return attributes;
  }
  
  @Override
  public String getUsername() {
    return username; //UserDetails 인터페이스 구현: 사용자명 (email) 반환
  }
  @Override
  public String getPassword() {
    return password;  // OAuth2 로그인 사용자일 경우 null, 일반 로그인은 암호 반환
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
  
  @Override
  public boolean isAccountNonExpired() {
    return true; //계정이 만료되지 않음을 명시
  }
  
  @Override
  public boolean isAccountNonLocked() {
    return true;//계정이 잠겨 있지 않음을 명시
  }
  
  @Override
  public boolean isCredentialsNonExpired() {
    return true; //자격증명이 만료되지 않음을 명시
  }
  
  @Override
  public boolean isEnabled() {
    return true;
  }
  
  public Long getUserId() {
    return userId;
  }
  
  /*
  * 로그인 후 컨트롤러에서 사용자 정보를 가져올 때
  * @AuthenticationPrincipal CustomUser customUser
  * */
}
