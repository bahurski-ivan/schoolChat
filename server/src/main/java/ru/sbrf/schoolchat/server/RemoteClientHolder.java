package ru.sbrf.schoolchat.server;

import ru.sbrf.schoolchat.objects.TextMessage;
import ru.sbrf.schoolchat.remoteclient.RemoteClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 17/11/2016.
 */
class RemoteClientHolder implements RemoteClient {
    private final ListenerWorker myWorker;

    private String userName;
    private List<TextMessage> privateMessages = new ArrayList<>();

    RemoteClientHolder(ListenerWorker myWorker) {
        this.myWorker = myWorker;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String name) {
        this.userName = name;
    }

    @Override
    public void send(Object o) {
        myWorker.send(o);
    }

    @Override
    public boolean isAuthenticated() {
        return userName != null;
    }

    @Override
    public void setAuthenticated(boolean flag) {
        // user name used for this
    }

    @Override
    public void sendPrivate(TextMessage message) {
        privateMessages.add(message);
        send(message);
    }

    @Override
    public List<TextMessage> getPrivateMessages() {
        return privateMessages;
    }
}
