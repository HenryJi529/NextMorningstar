package com.morningstar.exception;

import com.morningstar.constant.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private Integer code = ResponseCode.ERROR.getCode();

    public BaseException(String message) {
        super(message);
    }

    public BaseException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
