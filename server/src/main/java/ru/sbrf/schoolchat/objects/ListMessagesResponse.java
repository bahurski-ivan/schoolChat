package ru.sbrf.schoolchat.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 17/11/2016.
 */
public class ListMessagesResponse {
    private List<TextMessage> messageList = new ArrayList<>();

    public ListMessagesResponse(List<TextMessage> messageList) {
        this.messageList = messageList;
    }

    public List<TextMessage> getMessageList() {
        return messageList;
    }
}
