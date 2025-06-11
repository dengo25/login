package com.example.devLogin.security.config;

import com.example.devLogin.security.handler.CustomLoginFailureHandler;
import com.example.devLogin.security.handler.CustomLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration //설정클래스임을 spring에게 알림
@EnableWebSecurity //spring security 웹 보안 활정화
@RequiredArgsConstructor
public class SecurityConfig {
  
  private final OAuth2UserService customOAth2UserService;  //OAuth2 로그인 사용자 정보처리 서비스
  
  @Bean //비밀번호 암호화를 위한 Bean등록
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); //BCrypt 알고리즘을 사용한 PasswordEncoder 반환
  }
  
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/users/register", "/login", "/css/**", "/js/**").permitAll() //누구나 접근 허용
            .anyRequest().authenticated() //위 요청외에는 인증 필요
        )
        //OAuth2 로그인 성정
        .oauth2Login(oauth2 -> oauth2
            .loginPage("/login")  //OAuth2 로그인도 동일한 로그인 페이지 사용
            .successHandler(authenticationSuccessHandler()) //로그인 성공 시 실행할 핸들러
            .failureHandler(authenticationFailureHandler())
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAth2UserService) //사용자 정보 처리를 위한 커스텀 서비스 지정
            )
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout") //로그아웃 성공 시 이동할 URL
            .permitAll() //로그아웃은 인증 없이도 가능
        );
    
    return http.build(); //설정 완료 후 securityFilterChain 반환
  }
  
/*  로그인 성공 시 실행될 핸들러 등록
  스프링 시큐리티는 기본적으로 로그인 성공시 "/"로, 실패하면 로그인 페이지로 돌아온다.
  그래서 핸들러로 처리해준다.
*/
  @Bean
  AuthenticationSuccessHandler authenticationSuccessHandler() {
    return new CustomLoginSuccessHandler();
  }
  
  //로그인 실패 시 실행될 핸들러 등록
  @Bean
  AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomLoginFailureHandler();
  }
}
