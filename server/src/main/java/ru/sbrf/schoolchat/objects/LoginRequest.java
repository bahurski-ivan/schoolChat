package ru.sbrf.schoolchat.objects;

/**
 * Created by Ivan on 16/11/2016.
 */
public class LoginRequest {
    private String requestedName;

    public LoginRequest(String requestedName) {
        this.requestedName = requestedName;
    }

    public String getRequestedName() {
        return requestedName;
    }
}
