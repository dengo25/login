package com.example.devLogin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class UserEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String username;
  
  private String password;
  
  private String role;
  
  private String authProvider;}
