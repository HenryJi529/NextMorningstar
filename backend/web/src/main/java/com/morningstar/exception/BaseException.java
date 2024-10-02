package com.morningstar.exception;

import com.morningstar.response.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private String code = ResponseCode.ERROR.name();

    public BaseException(String message) {
        super(message);
    }

    public BaseException(ResponseCode responseCode, Object... arguments) {
        super((arguments != null && arguments.length > 0)
                ? String.format(responseCode.getMessage(), arguments)
                : responseCode.getMessage());
        this.code = responseCode.name();
    }
}
