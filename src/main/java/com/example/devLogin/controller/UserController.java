package com.example.devLogin.controller;


import com.example.devLogin.entity.User;
import com.example.devLogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.regex.Pattern;

@Controller
@RequestMapping("/users")
public class UserController {
  
  private final UserService userService;
  
  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  //회원 가입 폼
  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new User()); //빈 User객체를 모델에 추가하여 폼에서 바인딩 할 수 있게 함
    return "register";
  }
  
  @PostMapping("/register")
  public String registerUser(@ModelAttribute("user") User user,
                             BindingResult result, //유효성 검사 결과를 담는 객체
                             Model model) {
    
    //이메일 형식을 검증하기 위한 정규 표현식
    String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    
    //사용지 이름이 정규식과 일치하지 않으면 에러 처리
    if (!Pattern.matches(emailPattern, user.getUsername())) {
      result.rejectValue("username", "error.user",
          "Invalid email format. Please enter a valid email address."); //BindingResult에 에러 등록
      model.addAttribute("emailError",
          "Invalid email format. Please enter a valid email address."); //뷰에 표시할 에러 메시지 추가
    }
    
    //에러 존재시 다시 회원가입 페이지로 이동
    if (result.hasErrors()) {
      return "register";
    }
    
    //이미 존재하는 사용자명(이메일)이 있는지 확인
    if (userService.findByUsername(user.getUsername()).isPresent()) {
      model.addAttribute("error", "Username already exists");
      return "register";
    }
    
    //사용자 등록처리
    userService.registerUser(user.getUsername(), user.getPassword());
    return "redirect:/login";
  }
  
}