package com.example.devLogin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(
    uniqueConstraints = { @UniqueConstraint(columnNames = "username") } //username 컬럼은 유일해야 함
)
public class UserEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String username;
  
  private String password;
  
  private String role;
  
}
