package com.thehoick.audiopila.exc;

public class DAOException extends Exception {
    private final Exception originalException;

    public DAOException(Exception originalException, String message) {
        super(message);
        this.originalException = originalException;
        originalException.printStackTrace();
    }
}
