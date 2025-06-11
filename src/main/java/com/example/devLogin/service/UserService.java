package com.example.devLogin.service;


import com.example.devLogin.entity.User;
import com.example.devLogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
  
  private final UserRepository userRepository;
  private PasswordEncoder passwordEncoder; //비밀번호 암호화를 위한 인코더
  
  //사용자 등록
  public User registerUser(String username, String password) {
    
    //이미 동일한 username이 존재하는지 확인
    if (userRepository.findByUsername(username).isPresent()) {
      throw new RuntimeException("Username already exists"); //존재하면 예외 발생
    }
    
    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password)); //비밀번호 암호화 후 설정
    return userRepository.save(user); //DB 저장 후 객체 반환
  }
  
  
  
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username); //존재 여부에 따라 Optional로 반환
  }
  
}