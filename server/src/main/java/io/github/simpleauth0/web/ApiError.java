package io.github.simpleauth0.web;

import lombok.Data;

/**
 * @author: ReLive27
 * @date: 2025/11/19 23:22
 */
@Data
public class ApiError {
    private Integer code;
    private String errorCode;
    private String message;
    private long timestamp = System.currentTimeMillis();


    public static ApiError fail(Integer code, String errorCode, String message) {
        ApiError error = new ApiError();
        error.code = code;
        error.errorCode = errorCode;
        error.message = message;
        return error;
    }

    public static ApiError fail(Integer code, String message) {
        ApiError error = new ApiError();
        error.code = code;
        error.errorCode = "";
        error.message = message;
        return error;
    }
}
