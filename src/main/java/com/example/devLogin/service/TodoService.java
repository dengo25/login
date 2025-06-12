package com.example.devLogin.service;

import com.example.devLogin.entity.TodoEntity;
import com.example.devLogin.respository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {
  
  private final TodoRepository repository;
  
  //새로운 Todo 항목을 생성하고, 해당 사용자 ID의 모든 Todo 목록 반환
  public List<TodoEntity> create(final TodoEntity entity) {
    validate(entity); //유효성 검사 항상 첫번째로 검증하는 게 좋다.
    
    repository.save(entity);
    
    log.info("Entity Id : {} is saved.", entity.getId());
    
    return repository.findByUserId(entity.getUserId()); // 사용자 ID로 할 일 목록 조회 및 반환
  }
  
  //TOdo 엔티티에 대한 유효성 검사 메서드
  private void validate(final TodoEntity entity) {
    if (entity == null) {
      log.warn("Entity cannot be null.");
      throw new RuntimeException("Entity cannot be null.");
    }
    
    if (entity.getUserId() == null) { //userId가 없으면 예외 처리
      log.warn("Unknown user.");
      throw new RuntimeException("Unknown user.");
    }
  }
  
  
  //특정 사용자의 모든 Todo 항목을 조회
  public List<TodoEntity> retrieve(final Long userId) {
    return repository.findByUserId(userId);
  }
  
  //기존 Todo 항목을 수정하고, 수정 후 해당 사용자 ID의 Todo 목록 반환
  public List<TodoEntity> update(final TodoEntity entity) {
    validate(entity);
    
    //DB에서 기존 엔티티 조회
    final Optional<TodoEntity> original = repository.findById(entity.getId());
    
    //존재할 경우 제목과 완료 여부만 수정
    original.ifPresent(todo -> {
      todo.setTitle(entity.getTitle());
      todo.setDone(entity.isDone());
      
      repository.save(todo);
    });
    
    //사용자 Id로 전체 목록 반환
    return retrieve(entity.getUserId());
  }
  
  //Todo 항목을 삭제하고, 삭제 후 해당 사용자 ID의 Todo 목록 반환
  public List<TodoEntity> delete(final TodoEntity entity) {
    validate(entity);
    
    try {
      repository.delete(entity);
    } catch (Exception e) {
      log.error("error deleting entity ", entity.getId(), e);
      
      throw new RuntimeException("error deleting entity " + entity.getId());
    }
    
    return retrieve(entity.getUserId());
  }
  
}
