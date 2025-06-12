package com.example.demo.exceptions;

import lombok.Getter;

@Getter
public class GenericException extends RuntimeException {

    private final String errorMsg;

    public GenericException(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

}