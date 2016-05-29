package com.thehoick.audiopila.testing;

public class MainResponse {
    private final int status;
    private final String body;

    public MainResponse(int status, String body) {

        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
