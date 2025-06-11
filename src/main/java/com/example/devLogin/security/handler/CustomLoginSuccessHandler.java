package com.example.devLogin.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
//로그인 성공 시 실행되는 커스텀 핸들러 클래스
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
  
  @Override
  //사용자가 로그인에 성공하면 spring security가 자동으로 이 메서드 호출
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication
  ) throws IOException, ServletException {
    
    log.info("onAuthenticationSuccess");
    
    String targetUrl = "/"; //로그인 성공 후 이동할 기본 url 설정
    response.sendRedirect(targetUrl);
  }
  
}