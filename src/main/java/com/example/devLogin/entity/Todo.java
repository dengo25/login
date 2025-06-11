package com.example.devLogin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "todos")
public class Todo {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String title;
  
  private String description;
  private boolean completed;
  
  @ManyToOne  //다대일 관계 - 하나의 사용자는 여러 개의 할 일을 가질 수 있음.
  @JoinColumn(name = "user_id",nullable = false)
  private User user;
}
