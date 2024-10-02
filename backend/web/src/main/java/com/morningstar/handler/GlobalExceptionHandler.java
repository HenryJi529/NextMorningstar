package com.morningstar.handler;

import cn.hutool.http.HttpStatus;
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private void logExceptionChain(Throwable throwable, int depth) {
        int maxDepth = 7;
        if (throwable == null || depth > maxDepth) {
            return;
        }

        System.out.printf("%s%s: %s%n", "-".repeat(depth*10), throwable.getClass().getSimpleName(), throwable.getMessage());
        if(depth == maxDepth){
            System.out.printf("%s ... ... ... ... %n", "-".repeat((depth+1)*10));
        }
        if (throwable.getCause() != null) {
            logExceptionChain(throwable.getCause(), depth + 1);
        }
    }
    /**
     * 捕获未知异常
     */
    @ExceptionHandler
    @SuppressWarnings("rawtypes")
    public R unknownExceptionHandler(RuntimeException ex) {
        log.error("捕获到未知异常\n堆栈跟踪如下:", ex);
        System.out.println("异常链如下: ");
        logExceptionChain(ex, 0);
        return R.error(String.format("未知异常信息: %s", ex.getMessage()));
    }

    /**
     * 捕捉业务异常
     */
    @ExceptionHandler
    public R<Object> baseExceptionHandler(BaseException ex) {
        log.error("业务异常信息: {}", ex.getMessage());
        return R.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕捉requestParam字段校验异常
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<Object> handleValidationExceptions(HandlerMethodValidationException ex) {
        List<String> errorMessages = Arrays.stream(ex.getDetailMessageArguments()).map(error -> {
            String errorMessage = ((String) error);
            return errorMessage.replace("and ", "");
        }).toList();
        return R.error(-HttpStatus.HTTP_BAD_REQUEST, "请求参数错误: " + String.join(";", errorMessages));
    }

    /**
     * 捕捉requestBody字段校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        return R.error(-HttpStatus.HTTP_BAD_REQUEST, errorMessage);
    }

    /**
     * 捕捉requestBody反序列化异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Object> handleJsonParseError(HttpMessageNotReadableException ex) {
        return R.error(-HttpStatus.HTTP_BAD_REQUEST, "请求体解析失败: " + Objects.requireNonNull(ex.getRootCause()).getMessage());
    }

    /**
     * 捕捉邮件发送错误
     */
    @ExceptionHandler({MailSendException.class, MailParseException.class})
    public R<Object> mailSendExceptionHandler() {
        return R.error("邮件发送出错");
    }

    /**
     * Multipart文件过大
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Object> multipartFileSizeExceededExceptionHandler() {
        return R.error("Multipart文件过大");
    }

    /**
     * 捕捉405错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Object> handle405(HttpRequestMethodNotSupportedException ex) {
        return R.error(-HttpStatus.HTTP_BAD_METHOD, "请求方法错误，支持的方法有: " + Arrays.toString(ex.getSupportedMethods()));
    }

    /**
     * 捕捉其他400错误
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, MissingServletRequestPartException.class, MultipartException.class})
    public R<Object> handle400(Exception ex) {
        return R.error(-HttpStatus.HTTP_BAD_REQUEST, "请求参数错误: " + ex.getMessage());
    }

    /**
     * 捕捉404错误
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    public R<Object> handle404(Exception ex) {
        return R.error(-HttpStatus.HTTP_NOT_FOUND, "资源不存在: " + ex.getMessage());
    }
}
