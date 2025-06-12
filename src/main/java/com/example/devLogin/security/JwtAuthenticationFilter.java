package com.example.devLogin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
//HTTP 요청에서 JWT토큰을 추출하고 인증 정보를 설정하는 필터 클래스
//클라이언트로부터 요청이 들어 올 때 마다 jwt토큰을 확인하고, 사용자 인증 정보를 설정하는 필터 역할을 한다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
  private final TokenProvider tokenProvider; //토큰 검증 및 사용자 ID추출을 위한 TokenProvider 의존성
  
  
  //OPTION 요청은 필터를 건너 뛰도록 설정 (CORS 사정 요청 등 무시)
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    if (request.getMethod().equals("OPTIONS")) {
      //OPTION는 cors 사전 요청을 의미한다
      //클라이언트가 실제 요청전에 options 요청을 보내서, 서버가 이 요청을 허용할지 확인하는 과정인데
      //이 과정에서 jwt인증 필터를 거칠 필요가 없기에 설정
      return true; //OPRIONS 메서드는 필터 대상 아님
    }
    
    return false; //filter가 작동
  }
  
  
/*  필터 내부 로직: 요청에서 JWT파싱 -> 검증 -> 인증 객체 생성 -> SecurityContext 설정
  사용자의 요청을 들어올 때 jwt토큰을 꺼내서 검증하고 정삭적인 토큰이면 해당 사용자를 인증된 상태로 만들어주는 역할
  1. 요청헤더에서 jwt토큰 추출
  2. 추출한 토큰이 유효한지 확인
  3. 유효한 경우 토큰에 담긴 사용자 아이디를 바탕으로 spring security에 인증 객체를 생성
  4. 인증 정보를 바탕으로 SecurityContext에 설정
  5. 다음 필터로 요청을 넘김
*/
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    
    try {
      String token = parseBearerToken(request); //Authorization 헤더에서 JWT 토큰 파싱
      
      log.info("doFilterInternal");
      
      //토큰이 존재하고 "null"이 아닌 경우
      if (token != null && !token.equalsIgnoreCase("null")) {
        String userId = tokenProvider.validateAndGetUserId(token); //토큰 검증 및 사용자 ID 추출
        log.info("Authenticated user ID : " + userId);
        
        //사용자 ID를 기반으로 인증 객체 생성(권한 없음)
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userId, null, AuthorityUtils.NO_AUTHORITIES);
        
        //요청 정보 추가(IP, 세션 등)
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        //새로운 SecurityContext 생성 및 인증 객체 설정
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        
        //현재 쓰레드에 SecurityContext 등록
        SecurityContextHolder.setContext(securityContext);
      }
    } catch (Exception ex) {
      log.error("Could not set user authentication in security context", ex);
    }
    
    //다음 필터로 요청 전달
    filterChain.doFilter(request, response);
  }
  
  
  //Authorization헤더에서 Bearer 토큰만 추출하는 메서드
  private String parseBearerToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    
    //헤더 값이 있고 "Bearer "로 시작하면 토큰만 추출하여 반환
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    
    return null;
  }
}
