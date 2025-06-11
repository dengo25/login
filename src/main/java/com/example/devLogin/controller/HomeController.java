package com.example.devLogin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  
  @GetMapping("/")
  public String home() {
    return "index"; //index라는 이름의 뷰(템플릿) 반환
  }
  
  @GetMapping("/login")
  public String login() {
    return "login";
  }
  
  @GetMapping("/logout")
  public String logout() {
    return "redirect:/login?logout"; //로그아웃 후 "/login?logout"경로로 리다이렉트
    //?logout 쿼리 파라미터는 프론트에서 확인해서 성공메세지 등을 반환하기 위해서
  }
}