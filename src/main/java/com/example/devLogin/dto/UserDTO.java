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
  private String password; //응답에 사용시 jsonignore등으로 처리해야
  private Long id;
  
  /*
  * 1. 로그인 요청시
  * 2. 로그인 응답 시에 사용
  * */
}
