package com.github.rumble.exception;

public class RuntimeException extends BaseException {
    private java.lang.RuntimeException e;

    public RuntimeException(java.lang.RuntimeException e) {
        super(e.getMessage());
    }

    public java.lang.RuntimeException getException() {
        return e;
    }
}
