package com.example.devLogin.security.service;

import com.example.devLogin.entity.User;
import com.example.devLogin.repository.UserRepository;
import com.example.devLogin.security.vo.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
//Spring Security에서 사용자 인증 정보를 불러오기 위한 서비스 클래스
//Spring Security가 로그인 시점에 자동으로 이 클래스를 찾아서 loadUserByUsername메서드를 호출한다.
//이 안에서 우리가 직접 만든 CustomUserDetails 객체를 만들어서 리턴 해주면 이걸 기반으로 사용자 인증을 진행
//이 클래스는 로그인 인증 흐름의 시작점이다.
public class CustomUserDetailsService implements UserDetailsService {
  
  @Autowired
  private UserRepository userRepository; //DB에서 사용자 정보를 로딩
  
  //username(email)을 기준으로 사용자 정보를 로딩하는 메서드
  @Override //로그인 폼에서 username(email)을 받아서 실행
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("CustomUserDetailsService.loadUserByUsername");
    
    //userName으로 사용자 정보를 조회, 없으면 예외 발생
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
    Long userId = user.getId();
    String email = user.getUsername();
    String password = user.getPassword();
    
    //권한 리스트 생성 및 "ROLE_USER"권한 추가
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    authorities.add(authority);
    
    //사용자 정보를 담은 CustomUserDetails 객체 생성
    CustomUserDetails customUserDetails = new CustomUserDetails(userId, email, password, authorities);
    
    return customUserDetails;
  }
  
}
