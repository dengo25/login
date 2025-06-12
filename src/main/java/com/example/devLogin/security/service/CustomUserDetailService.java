package com.example.devLogin.security.service;

import com.example.devLogin.entity.User;
import com.example.devLogin.repository.UserRepository;
import com.example.devLogin.security.vo.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
//spring Security의 UserDetailService구현체로서 일반 로그인 사용저 정보를 로드하는 역할
//일반 로그인 전용 클래스, UsernamePasswordAuthenticationFilter에서 로그인 시도시 호출
public class CustomUserDetailService implements UserDetailsService {
  
  private final UserRepository userRepository;
  
  //Spring Security가 로그인 시 호출하는 메서드 -> username(email)로 사용자 정보를 조회함
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("CustomUserDetailsService loadUserByUsername");
    
    // 사용자 email로 사용자 엔티티 조회, 없으면 예외 발생
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    //사용자 정보 추출
    Long userId = user.getId();
    String email = user.getUsername();
    String password = user.getPassword();
    
    //사용자 권한 설정
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); //기본 권한 부여
    authorities.add(authority);
    
    //CustomUser 객체 생성: UserDetails 및 OAuth2User를 구현한 사용자 객체
    CustomUser customUser = new CustomUser(userId, email, password, authorities);
    
    return customUser; //인증 처리를 위해 반환
  }
  
}
