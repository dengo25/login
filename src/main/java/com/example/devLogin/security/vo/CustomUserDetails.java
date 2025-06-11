package com.example.devLogin.security.vo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
//Spring Security에서 사용자 정보를 담는 UserDetails 구현 클래스
public class CustomUserDetails implements UserDetails {
  
  //직렬화란 객체를 바이트 단위로 변환해서 파일이나 네트워크로 전송 할 수 있도록 만듬
  //보통 세션저장이나 캐싱같은데서 구현
  private static final long serialVersionUID = 1L; //직렬화 버전 UID
  
  private final Long userId;
  private final String username;
  private final String password;
  
  //사용자의 권한 정보 반환
  private final Collection<? extends GrantedAuthority> authorities; //인가 처리를 할 때 핵심적으로 사용하는 값
  
  
  //사용자 권한 목록 정보 반환
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
  
  //사용자 비밀번호 반환
  @Override
  public String getPassword() {
    return password;
  }
  
  //사용자 이름 반환
  @Override
  public String getUsername() {
    return username;
  }
  
  //커스텀 메서드: 사용자 ID반환
  //userDetails에는 userId 필드가 없기에 직접 구현
  public Long getUserId(){
    return userId;
  }
}
