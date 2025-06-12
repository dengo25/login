package com.example.devLogin.respository;

import com.example.devLogin.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  
  UserEntity findByUsername(String username);
  
  Boolean existsByUsername(String username);
  
  UserEntity findByUsernameAndPassword(String username, String password);
  
}
