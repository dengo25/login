package com.example.devLogin.controller;

import com.example.devLogin.dto.ResponseDTO;
import com.example.devLogin.dto.TodoDTO;
import com.example.devLogin.entity.TodoEntity;
import com.example.devLogin.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("todo")
public class TodoController {
  
  private final TodoService service;
  
  
  //DTO -> entity -> service -> DTO -> Response
  @PostMapping
  //@AuthenticationPrincipal 을 통해서 사용자 정보를 바로 갖고옴
  public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
    try {
      TodoEntity entity = TodoDTO.toEntity(dto); //DTO -> entity 변환
      entity.setId(null); //새로운 엔티티이므로 id는 null 처리
      entity.setUserId(Long.parseLong(userId)); //인증된 사용자 id 설정
      
      List<TodoEntity> entities = service.create(entity); //서비스 호출
      
      
      //엔티티 리스트 -> DTO 리스트로 변환
      List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
      
      //정상 응답 객체 생성
      ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
      
      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      String error = e.getMessage();
      ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
      return ResponseEntity.badRequest().body(response);
    }
  }
  
  @GetMapping
  public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
    List<TodoEntity> entities = service.retrieve(Long.parseLong(userId));
    
    List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
    
    ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
    
    return ResponseEntity.ok().body(response);
  }
  
  @PutMapping
  public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
    TodoEntity entity = TodoDTO.toEntity(dto);
    entity.setUserId(Long.parseLong(userId));
    
    List<TodoEntity> entities = service.update(entity);
    
    List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
    
    ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build(); //응답 객체 생성
    
    return ResponseEntity.ok().body(response);
  }
  
  
  @DeleteMapping
  public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
    try {
      TodoEntity entity = TodoDTO.toEntity(dto);
      entity.setUserId(Long.parseLong(userId));
      
      List<TodoEntity> entities = service.delete(entity);
      
      List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
      
      ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
      
      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      String error = e.getMessage();
      ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
      return ResponseEntity.badRequest().body(response);
    }
  }
  
}
