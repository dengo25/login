package com.example.devLogin.respository;

import com.example.devLogin.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity,Long> {
  
  //특정 사용자(userId)에 속한 모든 Todo 목록을 조회하는 메서드
  List<TodoEntity> findByUserId(Long userId);
  
  //JPQL을 사용하여 userId를 단일 TodoEntity를 조회하는 커스텀 쿼리
  @Query("SELECT t FROM TodoEntity t WHERE t.userId = ?1") //?1은 첫번 째 파라미터를 의미
  TodoEntity findByUserIdQuery(Long userId); //직접 정의한 JPQL 쿼리 실행
  
}
