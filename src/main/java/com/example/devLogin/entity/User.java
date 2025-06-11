package com.example.devLogin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String username;
  
  private String password; //암호화해서 저장해야
  
  private String socialType;
  private String socialId;
  
  // User와 할 일 간의 1:N 관계 매핑
  @OneToMany(mappedBy = "user") //Todo엔티티의 "user"필드를 기준으로 관계를 설정함.
  private Set<Todo> todos; //사용자가 소유한 할 일 목록 Set을 쓴이유는 할 일이 중복돼서 저장하지 않기 위해서
  
}
