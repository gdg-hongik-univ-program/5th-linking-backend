package com.gdg.linking.global.exception.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    //에러메시지
    private String message;
}
