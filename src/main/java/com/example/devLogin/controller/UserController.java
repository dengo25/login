package com.example.devLogin.controller;

import com.example.devLogin.dto.ResponseDTO;
import com.example.devLogin.dto.UserDTO;
import com.example.devLogin.entity.UserEntity;
import com.example.devLogin.security.TokenProvider;
import com.example.devLogin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {
  
  private final UserService userService;
  private final TokenProvider tokenProvider;  //JWT토큰 생성 유틸
  
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //비밀번호 암호화를 위한 인코더
  
  
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
    try {
      if (userDTO == null || userDTO.getPassword() == null) {
        throw new RuntimeException("Invalid Password value.");
      }
      
      UserEntity user = UserEntity.builder()
          .username(userDTO.getUsername())
          .password(passwordEncoder.encode(userDTO.getPassword()))
          .build();
      
      UserEntity registeredUser = userService.create(user);
      UserDTO responseUserDTO = UserDTO.builder()
          .id(registeredUser.getId())
          .username(registeredUser.getUsername())
          .build();
      
      return ResponseEntity.ok().body(responseUserDTO);
    }
    catch (Exception e) {
      ResponseDTO responseDTO = ResponseDTO.builder()
          .error(e.getMessage())
          .build();
      
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
  
  @PostMapping("/signin")
  public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
    UserEntity user = userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(), passwordEncoder);
    
    if (user != null) {
      final String token = tokenProvider.create(user);
      
      final UserDTO responseUserDTO = UserDTO.builder()
          .username(user.getUsername())
          .id(user.getId())
          .token(token)
          .build();
      
      return ResponseEntity.ok().body(responseUserDTO);
    }
    else {
      ResponseDTO responseDTO = ResponseDTO.builder()
          .error("Login failed.")
          .build();
      
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
  
}
