package com.example.devLogin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
  
  private String token; //인증 토큰 등 로그인 후 클라이언트에 전달됨
  private String username; //사용자명이나 이메일
  private String password;
  private Long id;
  
}
