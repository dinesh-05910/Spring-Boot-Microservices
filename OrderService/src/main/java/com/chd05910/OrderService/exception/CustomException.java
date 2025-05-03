package com.chd05910.OrderService.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {
    private int status;
    private String errorCode;
    public CustomException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
