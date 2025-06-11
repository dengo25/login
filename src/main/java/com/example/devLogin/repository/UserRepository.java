package com.example.devLogin.repository;

import com.example.devLogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  
  //존재하지 않을 수도 있으므로 Optional로 감싸서 반환
  Optional<User> findByUsername(String username); //username필드를 기준으로 User 조회
}
