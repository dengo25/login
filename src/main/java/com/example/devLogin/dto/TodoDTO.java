package com.example.devLogin.dto;

import com.example.devLogin.entity.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TodoDTO {
  
  private Long id;
  private String title;
  private boolean done;
  
  //Entity 객체를 받아 DTO를 생성하는 생성자
  public TodoDTO(final TodoEntity entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.done = entity.isDone();
  }
  
  //DTO 객체를 엔티티 객체로 변환하는 정적 메서드
  //ex 클라이언트에서 json 형식으로 할 일 추가 요청을 보내면
  // DTO로 받고  -> entity로 변경
  public static TodoEntity toEntity(final TodoDTO dto) {
    return TodoEntity.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .done(dto.isDone())
        .build();
  }
  
}
