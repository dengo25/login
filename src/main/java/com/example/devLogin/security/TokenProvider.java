package com.example.devLogin.security;

import com.example.devLogin.entity.UserEntity;
import com.example.devLogin.security.vo.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
//JWT 토큰을 생성하고 검증하는 역할
public class TokenProvider {
  
  //JWT 서명에 사용할 비밀 키
  private static final String SECRET_KEY = "FlRpX30pMqDbiAkmlfArbrmVkDD4RqISskGZmBFax5oGVxzXXWUzTR5JyskiHMIV9M1Oicegkpi46AdvrcX1E6CmTUBc6IFbTPiD";
  
  //비밀 키로부터 HMAC SHA 키 객체 생성
  //JWT 의 위,변조를 막고 유효한 토큰인지 확인할수 있도록 해줌
  private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  
  //사용자 정보를 기반으로 JWT토큰 생성
  public String create(UserEntity userEntity) {
    
    //토큰 만료 시간을 현재 시각으로부터 1일 뒤로 설정
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    
    //JWT생성 및 반환
    return Jwts.builder()
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS512) //서명 알고리즘과 키 설정
        .setSubject(String.valueOf(userEntity.getId())) //사용자 ID를 subject로 설정
        .setIssuer("demo app") //토큰 발급자 정보 설정
        .setIssuedAt(new Date()) //토큰 발급 시간 설정
        .setExpiration(expiryDate) //만료 시간 설정
        .compact();
  }
  
  //토큰을 검증하고 포함된 사용자 ID(subject)를 반환
  //토큰 내부에서 사용자 id를 꺼내주는 메서드
  public String validateAndGetUserId(String token) {
    
    //토큰 파싱, 검증(서명이 유효한지 확인)
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(SIGNING_KEY) //서명 키 설정
        .build()
        .parseClaimsJws(token)
        .getBody(); //Payload(Claims)추출
    
    return claims.getSubject(); //사용자 ID(subject) 반환
  }
  
  //사용자 ID만을 기반으로 토큰 생성(ex OAuth 사용자 등)
  public String createByUserId(final Long userId) {
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
        .compact();
  }
  
  //OAuth2 에서 사용하려고 만듬
  public String create(final Authentication authentication) {
    CustomUser userPrincipal = (CustomUser) authentication.getPrincipal();
    
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    
    return Jwts.builder()
        .setSubject(userPrincipal.getName())
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
        .compact();
  }
}