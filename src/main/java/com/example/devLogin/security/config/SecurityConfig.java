package com.example.devLogin.security.config;

import com.example.devLogin.security.handler.CustomLoginFailureHandler;
import com.example.devLogin.security.handler.CustomLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration //설정클래스임을 spring에게 알림
@EnableWebSecurity //spring security 웹 보안 활정화
public class SecurityConfig {
  
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
        .formLogin(form -> form
            .loginPage("/login")  //커스텀 로그인 페이지 경로
            .permitAll() //로그인 페이지는 인증 없이 접근 가능
            .defaultSuccessUrl("/todos", true) //로그인 성공시 "/todos"로 리다이렉트 (항상)
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll() //로그아웃은 인증 없이도 가능
        );
    
    return http.build(); //설정 완료 후 securityFilterChain 반환
  }
  
  //로그인 성공 시 실행될 핸들러 등록
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
