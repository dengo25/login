package com.example.devLogin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
//리디렉션 url을 쿠키에 저장하는 클래스
public class RedirectUrlCookieFilter extends OncePerRequestFilter { //요청 당 한 번만 실행되는 커스텀 필터 클래스

	public static final String REDIRECT_URI_PARAM = "redirect_url"; //요청 파라미터 및 쿠키 이름

	private static final int MAX_AGE = 180; //쿠키 유효 시간 180초

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// "/oauth2/authorization" 로 시작하는 요청이 들어오면 처리
		if (request.getRequestURI().startsWith("/oauth2/authorization")) {
			try {
				log.info("request uri {} ", request.getRequestURI());
				
				String redirectUrl = request.getParameter(REDIRECT_URI_PARAM); //request uri 파라미터 가져오기

				// 쿠키 생성 및 설정
				Cookie cookie = new Cookie(REDIRECT_URI_PARAM, redirectUrl); //쿠키 이름과 값 설정
				cookie.setPath("/"); //전체 경로에서 쿠키 사용 가능 하도록 설정
				cookie.setHttpOnly(true); //자바 스크립트에서 접근 불가능하도록 설정 (보안 강화)
				cookie.setMaxAge(MAX_AGE); //쿠키 만료시간
				response.addCookie(cookie); //응답에 쿠키 추가

			} catch (Exception ex) {
				log.error("Could not set user authentication in security context", ex);
				log.info("Unauthorized request");
			}

		}
		//다음 필터 체인으로 요청 전달
		filterChain.doFilter(request, response);
	}
	
}
