package com.example.devLogin.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.example.devLogin.security.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

@Slf4j
@AllArgsConstructor
@Component
//OAuth2 로그인 성공 시 처리 로직 담당 클래스
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private static final String LOCAL_REDIRECT_URL = "http://localhost:5173"; //기본 리다이렉트 주소

	//로그인 인증이 성공했을 때 호출되는 메서드
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		
		TokenProvider tokenProvider = new TokenProvider(); //JWT 토큰 발급을 위한 객체 생성
		String token = tokenProvider.create(authentication); //인증 정보를 기반으로 JWT 토큰 생성

		log.info("token {}", token);
		
		
		//요청에 포함된 쿠키 중 redirect_url 이름의 쿠키를 찾아 Optional로 매핑
		Optional<Cookie> oCookie = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM))
				.findFirst();
		
		//쿠키가 존재하면 값 추출, 없으면 Optional.empty
		Optional<String> redirectUri = oCookie.map(Cookie::getValue);

		log.info("redirectUri {}", redirectUri);

		//쿠키 값이 존재하면 해당 값 사용, 없으면 기본 주소 사용 후 JWT 토큰을 쿼리 파라미터로 추가
		String targetUrl = redirectUri.orElseGet(() -> LOCAL_REDIRECT_URL) + "/sociallogin?token=" + token;

		log.info("targetUrl {}", targetUrl);

		response.sendRedirect(targetUrl);
	}

}