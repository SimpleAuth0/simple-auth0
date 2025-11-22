package io.github.simpleauth0.web;

import io.github.simpleauth0.core.i18n.SimpleAuth0MessageSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: ReLive27
 * @date: 2025/11/19 23:02
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private MessageSourceAccessor messageSource = SimpleAuth0MessageSource.getAccessor();

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handlerMethodException(MethodArgumentNotValidException e) {
        log.error("参数校验异常", e);
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiError.fail(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID_PARAMETER, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handlerConfusionException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiError.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCodes.SERVER_ERROR, messageSource.getMessage("server.error", "系统错误，请联系管理员", LocaleContextHolder.getLocale())));
    }

}
