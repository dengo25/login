package com.example.devLogin.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> { //제네릭으로 공통 API응답 포맷을 제공
  
  private String error; //에러 메세지
  
  private List<T> data; //실제 응답 데이터 리스트(TodoDTO, UserDTO 등 다양한 타입 가능)
  
}
