package com.gdg.linking.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;

    // 성공 시 호출할 메서드
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "요청에 성공했습니다.", data);
    }
}