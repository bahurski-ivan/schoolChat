package ru.sbrf.schoolchat.objects;

/**
 * Created by Ivan on 16/11/2016.
 */
public class TextMessage {
    private String from;
    private String message;

    public TextMessage(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return from + " >> " + message;
    }
}
