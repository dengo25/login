package com.example.devLogin.service;

import com.example.devLogin.entity.Todo;
import com.example.devLogin.entity.User;
import com.example.devLogin.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor  //final 필드를 매개변수로 하는 생성자를 Lombok이 자동 생성
@Service //이 클래스가 서비스 계층 컴포넌트임을 명시 (스프링이 관리)
public class TodoService {
  
  private final TodoRepository todoRepository;  //할 일 관련 DB 접근을 위한 리포지토리
  
  // 새로운 Todo를 추가하고 Db에 저장하는 메서드
  public Todo addTodo(Todo todo, User user) {
    todo.setUser(user);
    return todoRepository.save(todo); //DB에 저장 후 저장된 객체 반환
  }
  
  
  
  public List<Todo> getTodosByUser(User user) {
    return todoRepository.findByUserId(user.getId());
  }
  
  
  
  public void deleteTodoById(Long id, User user) {
    //ID로 할 일을 조회, 없으면 예외 발생
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Todo not found"));
    
    // 현재 사용자와 해당 할 일의 소유자가 일치하지 않으면 예외 발생
    if (!todo.getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized");
    }
    todoRepository.deleteById(id);
  }
  
  
  
  public Optional<Todo> getTodoById(Long id) {
    return todoRepository.findById(id);
  }
  
  
  
  public void updateTodo(Long id, String title, String description, User user) {
    Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Todo not found"));
    
    //현재 사용자와 할 일의 소유자가 일치하지 않으면 예외 발생
    if (!todo.getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized");
    }
    
    todo.setTitle(title);
    todo.setDescription(description);
    todoRepository.save(todo);
  }
  
}