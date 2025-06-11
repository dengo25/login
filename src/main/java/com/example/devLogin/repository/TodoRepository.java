package com.example.devLogin.repository;


import com.example.devLogin.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
  
  //메서드 이름을 기반으로 spring Data JPA가 쿼리를 자동 생성함
  List<Todo> findByUserId(Long userId); //user_id로 할 일 목록을 조회
  //select * from todos where user_id = ?;
}
