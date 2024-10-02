package com.morningstar.handler;

import cn.hutool.http.HttpStatus;
import com.morningstar.exception.BaseException;
import com.morningstar.constant.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获未知异常
     */
    @ExceptionHandler
    @SuppressWarnings("rawtypes")
    public R unknownExceptionHandler(RuntimeException ex){
        ex.printStackTrace();
        String message = String.format("未知异常信息: %s", ex.getMessage());
        log.error(message);
        return R.error(message);
    }

    /**
     * 捕捉业务异常
     */
    @ExceptionHandler
    public R<Object> baseExceptionHandler(BaseException ex){
        log.error("业务异常信息: {}", ex.getMessage());
        return R.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕捉requestParam字段校验异常
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<Object> handleValidationExceptions(HandlerMethodValidationException ex) {
        List<String> errorMessages = Arrays.stream(ex.getDetailMessageArguments()).map(error -> (String) error).toList();
        return R.error(String.join(",", errorMessages));
    }

    /**
     * 捕捉requestBody字段校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        return R.error(errorMessage);
    }

    /**
     * 捕捉邮件发送错误
     */
    @ExceptionHandler({MailSendException.class, MailParseException.class})
    public R<Object> mailSendExceptionHandler(){
        return R.error("邮件发送出错");
    }

    /**
     * 捕捉405
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Object> handle405(HttpRequestMethodNotSupportedException ex) {
        return R.error(HttpStatus.HTTP_BAD_METHOD, "请求方法错误，支持的方法有: " + Arrays.toString(ex.getSupportedMethods()));
    }

    /**
     * 捕捉400
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    public R<Object> handle400() {
        return R.error(HttpStatus.HTTP_BAD_REQUEST, "请求参数错误");
    }

    /**
     * 捕捉404
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    public R<Object> handle404(){
        return R.error(HttpStatus.HTTP_NOT_FOUND, "资源不存在");
    }
}
