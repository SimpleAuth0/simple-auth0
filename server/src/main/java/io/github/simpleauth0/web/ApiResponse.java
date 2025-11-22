package io.github.simpleauth0.web;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author: ReLive27
 * @date: 2025/11/22 10:59
 */
@Data
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.code = HttpStatus.OK.value();
        apiResponse.message = "success";
        apiResponse.data = data;
        return apiResponse;
    }

    public static <T> ApiResponse<T> success(String code, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.code = HttpStatus.OK.value();
        apiResponse.message = message;
        apiResponse.data = null;
        return apiResponse;
    }
}
