package com.chd_05910.springbootdemo.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ErrorMessage {
    private HttpStatus status;
    private String errorMessage;

    public ErrorMessage() {

    }

    public ErrorMessage(HttpStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
