package ru.sbrf.schoolchat.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 17/11/2016.
 */
public class SendTextRequest {
    private String message;
    private List<String> recipients = new ArrayList<>();

    public SendTextRequest(String message, List<String> recipients) {
        this.message = message;
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}
