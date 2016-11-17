package ru.sbrf.schoolchat.remoteclient;

import ru.sbrf.schoolchat.objects.TextMessage;

import java.util.List;

/**
 * Created by Ivan on 17/11/2016.
 */
public interface RemoteClient {
    String getUserName();

    void setUserName(String name);

    void send(Object o);

    boolean isAuthenticated();

    void setAuthenticated(boolean flag);

    void sendPrivate(TextMessage message);

    List<TextMessage> getPrivateMessages();
}