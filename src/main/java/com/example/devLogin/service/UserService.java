package com.example.devLogin.service;

import com.example.devLogin.entity.UserEntity;
import com.example.devLogin.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
  
  private final UserRepository userRepository;
  
  public UserEntity create(final UserEntity userEntity) {
    //사용자 정보가 null이거나 username이 없으면 예외 발생
    if (userEntity == null || userEntity.getUsername() == null) {
      throw new RuntimeException("Invalid arguments");
    }
    
    final String username = userEntity.getUsername(); //사용자 명 추출
    
    // 이미 같은 사용자 명이 존재하는지 확인
    if (userRepository.existsByUsername(username)) {
      log.warn("Username already exists {}", username);
      throw new RuntimeException("Username already exists"); //중복 사용자 예외
    }
    
    return userRepository.save(userEntity); //사용자 정보 저장 및 반환
  }
  
  // 사용자 인증 메서드: username과 password를 비교하여 사용자 반환
  public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
    final UserEntity originalUser = userRepository.findByUsername(username); //사용자 명으로 사용자 조회
    
    // 사용자 존재 및 비밀번호 일치 여부 확인
    if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {// 입력값과 암호화 된 값을 비교해주는 방식
      return originalUser; //인증 성공 시 사용자 객체 반환
    }
    
    return null;
  }
  
}
