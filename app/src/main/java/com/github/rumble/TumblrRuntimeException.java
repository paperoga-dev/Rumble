package com.github.rumble;

class TumblrRuntimeException extends TumblrException {
    private RuntimeException e;

    public TumblrRuntimeException(RuntimeException e) {
        super(e.getMessage());
    }

    public RuntimeException getException() {
        return e;
    }
}
