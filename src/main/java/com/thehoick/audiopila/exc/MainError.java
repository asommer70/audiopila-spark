package com.thehoick.audiopila.exc;

public class MainError extends RuntimeException {
    private final int status;

    public MainError(int status, String msg) {
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
