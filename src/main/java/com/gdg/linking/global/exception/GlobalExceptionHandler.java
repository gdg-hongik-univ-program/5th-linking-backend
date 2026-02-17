package com.gdg.linking.global.exception;

import com.gdg.linking.global.exception.custom.BadRequestException;
import com.gdg.linking.global.exception.custom.NotFoundException;
import com.gdg.linking.global.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e){
         ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
         return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 추가: HttpStatusCodeException 전용 핸들러
    @ExceptionHandler(value = HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());

        // 예외에 담긴 상태 코드(401 등)를 그대로 꺼내서 설정
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }

    //모든 에러의 부모
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e){
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }



}
