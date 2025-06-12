package com.example.devLogin.respository;

import com.example.devLogin.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  
  UserEntity findByUsername(String username);
  
  Boolean existsByUsername(String username);
  
  //사용자 조회(로그인 검증에 사용가능)
  UserEntity findByUsernameAndPassword(String username, String password);
  /* 위 조회 쿼리는 매우 보안에 취약한 메서드임.
  그래서 안전한 로직은
  1. findByUsername(username)을 사용해서 해당 사용자를 찾고
  2. BCryptPaswordEncoder.matches() 같은 메서드를 이용해서 입력한 비밀번호와 저장된 비밀번호를 검증해야.
  * */
}
