package com.example.devLogin.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

@Slf4j
//로그인 실패 시 실행되는 커스텀 핸들러 클래스
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
  
  //인증 요청이 저장된 캐시에서 이전 요청을 가져오기 위한 객체
  //ex) 인증이 필요한 /todo 페이지를 직접 접속하려다 로그인 페이지로 이동되었되면 로그인피 실패하더라고
  // 원래 가려던 url로 다시 보내 줄 수 있게 값을 저장
  private RequestCache requestCache = new HttpSessionRequestCache();
  
  //로그인 실패시 호출되는 메서드
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, //클라이언트 요청
                                      HttpServletResponse response,
                                      AuthenticationException exception
  ) throws IOException, ServletException {
    
    //로그인 실패 예외를 로그로 출력
    log.info("onAuthenticationFailure");
    
    //사용자가 원래 가려던 요청 정보 가져오기
    log.info("onAuthenticationFailure exception " + exception);
    
    //사용자가 원래 가려던 요청 정보(페이지) 가져오기
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    
    if(savedRequest != null) {
      String targetUrl = savedRequest.getRedirectUrl(); //원래 요청한 url
      
      log.info("Login Failure targetUrl = " + targetUrl);
      
      //사용자가 가려던 url로 리다이렉트(로그인 실패 후에도 이동)
      response.sendRedirect(targetUrl);
    }
  }
  
}