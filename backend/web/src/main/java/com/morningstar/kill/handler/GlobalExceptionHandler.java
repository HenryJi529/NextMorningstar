package com.morningstar.kill.handler;

import com.morningstar.kill.exception.BaseException;
import com.morningstar.kill.pojo.vo.resp.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获未知异常
     */
    @ExceptionHandler
    @SuppressWarnings("rawtypes")
    public R unknownExceptionHandler(RuntimeException ex){
        log.error("未知异常信息：{}", ex.getMessage());
        return R.error(String.format("未知异常信息：%s", ex.getMessage()));
    }

    /**
     * 捕捉业务异常
     */
    @ExceptionHandler
    public R<Object> baseExceptionHandler(BaseException ex){
        log.error("业务异常信息：{}", ex.getMessage());
        return R.error(String.format("业务异常信息：%s", ex.getMessage()));
    }
}
