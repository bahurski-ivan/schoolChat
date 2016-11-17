package ru.sbrf.schoolchat.objects;

/**
 * Created by Ivan on 16/11/2016.
 */
public class LoginResponse {
    private boolean isOk;
    private String message;

    public LoginResponse(boolean isOk, String message) {
        this.isOk = isOk;
        this.message = message;
    }

    public boolean isOk() {
        return isOk;
    }

    public String getMessage() {
        return message;
    }
}
