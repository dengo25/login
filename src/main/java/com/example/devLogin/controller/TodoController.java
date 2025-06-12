package com.example.devLogin.controller;

import com.example.devLogin.entity.Todo;
import com.example.devLogin.entity.User;
import com.example.devLogin.security.vo.CustomUser;
import com.example.devLogin.service.TodoService;
import com.example.devLogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/todos")
public class TodoController {
  
  private final TodoService todoService;
  private final UserService userService;
  
  @GetMapping
  public String listTodos(Authentication authentication, Model model) {
    Object principal = authentication.getPrincipal(); //현재 인증된 사용자 정보 조회
    
    if (principal == null) {
      return "redirect:/login";
    }
    
    CustomUser customUser = (CustomUser) principal; //사용자 정보를 커스텀 객체로 캐스팅
    
    Optional<User> user = userService.findByUsername(customUser.getUsername()); //DB 조회
    if (user.isEmpty()) {
      return "redirect:/login";
    }
    
    List<Todo> todos = todoService.getTodosByUser(user.get()); //할 일 목록 조회
    model.addAttribute("todos", todos); //모델에 데이터 추가
    
    return "todos";
  }
  
  
  @PostMapping("/add")
  public String addTodo(Authentication authentication, @ModelAttribute Todo todo) {
    Object principal = authentication.getPrincipal(); //인증 정보 획득
    
    if (principal == null) {
      return "redirect:/login";
    }
    
    CustomUser customUser = (CustomUser) principal;
    
    User user = new User();
    user.setId(customUser.getUserId()); //현재 로그인한 사용자의 ID 설정
    
    todoService.addTodo(todo, user);
    return "redirect:/todos";
  }
  
  
  //삭제 메서드
  @PostMapping("/delete/{id}")
  public String deleteTodo(@PathVariable("id") Long id, Authentication authentication) {
    Object principal = authentication.getPrincipal();
    
    if (principal == null) {
      return "redirect:/login";
    }
    
    CustomUser customUser = (CustomUser) principal;
    
    User user = new User();
    user.setId(customUser.getUserId()); //현재 로그인한 사용자의 ID 설정
    
    todoService.deleteTodoById(id, user);
    return "redirect:/todos";
  }
  
  //수정하기
  @GetMapping("/edit/{id}")
  public String editTodo(@PathVariable("id") Long id, Model model, Authentication authentication) {
    Object principal = authentication.getPrincipal();
    
    if (principal == null) {
      return "redirect:/login";
    }
    
    CustomUser customUser = (CustomUser) principal;
    
    Long userId = customUser.getUserId();
    
    Optional<Todo> todo = todoService.getTodoById(id); //해당 ID의 할 일 조회
    
    //해당 todo가 존재하고 로그인한 소유자 소유인지 확인
    if (todo.isPresent() && todo.get().getUser().getId().equals(userId)) {
      model.addAttribute("todo", todo.get()); //모델에 할 일 정보 추가
      return "edit_todo"; //수정 폼 페이지 반환
    }
    return "redirect:/todos"; //조건 미 충족 시 목록으로 리다이렉트
  }
  
  
  //특정 Id의 할 일을 실제로 수정하는 메서드
  @PostMapping("/update/{id}")
  public String updateTodo(@PathVariable("id") Long id,
                           @RequestParam("title") String title, //새 제목
                           @RequestParam("description") String description, //새 설명
                           Authentication authentication) {
    Object principal = authentication.getPrincipal();
    
    if (principal == null) {
      return "redirect:/login";
    }
    
    CustomUser customUser = (CustomUser) principal;
    
    User user = new User();
    user.setId(customUser.getUserId()); //현재 로그인한 사용자의 ID 설정
    
    todoService.updateTodo(id, title, description, user);
    return "redirect:/todos";
  }
  
  /*
  * 보안 인증 및 처리 방식 요약
  * Authentication 객체에서 로그인 된 사용자 정보를 꺼냄
  * OAuth2 로그인 사용자의 경우 CustomOAuth2User 로 캐스팅
  * 사용자 ID로 할 일 소유자와 비교하여 권한 있는 사용자만 조작 가능
  *
  * 이를테면
  * 1. 사용자가 로그인 OAuth2->CustomOAuth2User 로 인증 정보저장
  * 2. 사용자가 /todos 접근 -> 해당 사용자에게 연결된 Todo 목록 조회
  * 3. 할 일 추가/삭제/수정 시 -> 사용자 ID로 검증 후 조작
  * */
}